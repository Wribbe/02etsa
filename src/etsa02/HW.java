package etsa02;

import hardware_interfaces.PincodeObserver;
import hardware_interfaces.BarcodeObserver;
import hardware_interfaces.BarcodePrinter;
import hardware_interfaces.BarcodeScanner;
import hardware_interfaces.ElectronicLock;
import hardware_interfaces.PincodeTerminal;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class HW implements HWAPI {

    private Core core;

    private long lastTimePushed;

    private Signal running_signal = null;

    public HW(Core core) {
        this.core = core;
        lastTimePushed = System.currentTimeMillis();
    }

    private List<Warden> wardens = new ArrayList<Warden>();

    private interface Validator {
        public boolean validate();
    }

    private class PincodeValidator implements Validator, PincodeObserver {

        private StringBuilder currentQueue = new StringBuilder();

        public void handleCharacter(char c) {
            if (c == '*') {
                currentQueue = new StringBuilder();
            } else {
                currentQueue.append(c);
            }
        }

        public boolean validate() {
            int stop = currentQueue.length();
            int start = stop-4;

            if (start < 0) {
                start = 0;
            }

            String current_pin = currentQueue.substring(start, stop);
            currentQueue = new StringBuilder();

            try {
                BikeOwner owner = core.userWithPin(current_pin);
                core.enter(owner);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
    }

    private class ScannerValidator implements Validator, BarcodeObserver {

        private boolean entry;
        private Barcode last_barcode;

        public ScannerValidator(boolean entry) {
            this.entry = entry;
        }

        public boolean validate() {
            if (last_barcode == null) {
                return false;
            }
            if (entry) {
                try {
                    core.park(last_barcode);
                } catch (ExceptionGarageFull e) {
                    return false;
                } catch (ExceptionAlreadyParked e) {
                    return false;
                }
                return core.barcodeRegistered(last_barcode.serial());
            } else {
                try {
                    core.unpark(last_barcode);
                } catch (ExceptionOwnerNotInGarage e) {
                    return false;
                }
            }
            return true;
        }

        public void handleBarcode(String barcode) {
            try {
                last_barcode = new Barcode(barcode);
            } catch (NumberFormatException e) {
                last_barcode = null;
            }
        }
    }

    private class Warden implements BarcodeObserver, PincodeObserver {

        private ElectronicLock lock;
        private PincodeTerminal terminal;
        private BarcodeScanner scanner;

        private Signal SIG_OK;
        private Signal SIG_ERR_NORMAL;
        private Signal SIG_ERR_BLOCKED;

        private int RED = PincodeTerminal.RED_LED;
        private int GREEN = PincodeTerminal.GREEN_LED;

        private Validator validator;

        private int invalid_inputs = 0;

        public boolean blocked = false;
        public Thread thread = null;

        private void setup() {
            SIG_OK = new Signal(this, terminal, GREEN, 20*1000, 0, 1);
            SIG_ERR_NORMAL = new Signal(this, terminal, RED, 1000, 1000, 3);
            SIG_ERR_BLOCKED = new Signal(this, terminal, RED, 2*60*1000, 0, 1);

            SIG_OK.prioritized = true;
            SIG_ERR_BLOCKED.blocker = true;
            SIG_ERR_BLOCKED.prioritized = true;
        }

        public Warden(PincodeTerminal terminal, ElectronicLock lock) {
            validator = new PincodeValidator();
            terminal.registerObserver(this);
            this.terminal = terminal;
            this.lock = lock;
            setup();
        }

        public Warden(BarcodeScanner scanner, ElectronicLock lock, boolean entry, PincodeTerminal output) {
            validator = new ScannerValidator(entry);
            scanner.registerObserver(this);
            this.scanner = scanner;
            this.terminal = output;
            this.lock = lock;
            setup();
        }

        public void handleBarcode(String barcode) {
            ((BarcodeObserver)validator).handleBarcode(barcode);
            if (validator.validate()) {
                lock.open(20);
                SIG_OK.start();
            } else {
                SIG_ERR_NORMAL.start();
            }
        }

        public void handleCharacter(char c) {
            if (c == '#') {
                if (validator.validate()) {
                    SIG_OK.start();
                    lock.open(20);
                    invalid_inputs = 0;
                } else {
                    invalid_inputs++;
                    if (invalid_inputs >= 10) {
                        SIG_ERR_BLOCKED.start();
                    } else {
                        SIG_ERR_NORMAL.start();
                    }
                }
            } else {
                ((PincodeObserver)validator).handleCharacter(c);
            }
        }

    }

    public void register_and_link(BarcodeScanner scanner, ElectronicLock lock, boolean entry, PincodeTerminal output) {
        wardens.add(new Warden(scanner, lock, entry, output));
    }

    public void register_and_link(PincodeTerminal terminal, ElectronicLock lock) {
        wardens.add(new Warden(terminal, lock));
    }

    private class Signal implements Runnable {

        private PincodeTerminal terminal;
        private long delay;
        private int color, flashes, done, duration;

        private Warden warden;

        protected boolean prioritized = false;
        protected boolean blocker = false;

        public Signal(Warden warden, PincodeTerminal terminal, int color, int duration, long delay, int flashes) {
            this.terminal = terminal;
            this.color = color;
            this.delay = delay;
            this.flashes = flashes;
            this.duration = duration;
            this.warden = warden;
        }

        public void start() {
            if (terminal == null) {
                return;
            }
            if (warden.thread == null || !warden.thread.isAlive()) {
                warden.thread = new Thread(this);
                warden.thread.start();
                if (blocker) {
                    warden.blocked = true;
                }
            } else {
                if (prioritized && !warden.blocked) {
                    warden.thread.interrupt();
                    warden.thread = new Thread(this);
                    warden.thread.start();
                }
            }
        }

        public void run() {
            done = 0;
            try {
                while (done < flashes) {
                    terminal.lightLED(color, duration/1000);
                    done++;
                    Thread.sleep(delay+duration);
                }
            } catch (InterruptedException e) {
                return;
            } finally {
               if (blocker && warden.blocked) {
                   warden.blocked = false;
               }
            }
        }
    }
}
