package javadoc;

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

        public ScannerValidator(boolean entry) {

        }

        public boolean validate() {
            return true;
        }

        public void handleBarcode(String barcode) {

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

        public Warden(PincodeTerminal terminal, ElectronicLock lock) {
            validator = new PincodeValidator();
            terminal.registerObserver(this);
            this.terminal = terminal;
            this.lock = lock;

            SIG_OK = new Signal(terminal, GREEN, 20*1000, 0, 1);
            SIG_ERR_NORMAL = new Signal(terminal, RED, 1000, 1000, 3);
            SIG_ERR_BLOCKED = new Signal(terminal, RED, 1000, 1000, 9);

            SIG_OK.prioritized = true;
        }

        public Warden(BarcodeScanner scanner, ElectronicLock lock, boolean entry) {
            validator = new ScannerValidator(entry);
            scanner.registerObserver(this);
            this.scanner = scanner;
            this.lock = lock;
        }

        public void handleBarcode(String barcode) {

        }

        public void handleCharacter(char c) {
            if (c == '#') {
                if (validator.validate()) {
                    lock.open(20);
                    SIG_OK.start();
                } else {
                    SIG_ERR_NORMAL.start();
                }
            } else {
                ((PincodeObserver)validator).handleCharacter(c);
            }
        }

    }

    public void register_and_link(BarcodeScanner scanner, ElectronicLock lock, boolean entry) {
        wardens.add(new Warden(scanner, lock, entry));
    }

    public void register_and_link(PincodeTerminal terminal, ElectronicLock lock) {
        wardens.add(new Warden(terminal, lock));
    }

    private Thread thread = null;

    private class Signal implements Runnable {

        private PincodeTerminal terminal;
        private long delay;
        private int color, flashes, done, duration;

        protected boolean prioritized = false;

        public Signal(PincodeTerminal terminal, int color, int duration, long delay, int flashes) {
            this.terminal = terminal;
            this.color = color;
            this.delay = delay;
            this.flashes = flashes;
            this.duration = duration;
        }

        public void start() {
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(this);
                thread.start();
            } else {
                if (prioritized) {
                    thread.interrupt();
                    thread = new Thread(this);
                    thread.start();
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
            }
        }
    }
}
