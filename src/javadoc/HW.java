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

    private int RED = PincodeTerminal.RED_LED;
    private int GREEN = PincodeTerminal.GREEN_LED;

    private long lastTimePushed;

    private StringBuilder currentQueue = new StringBuilder();
    private Signal running_signal = null;

    public HW(Core core) {
        this.core = core;
        lastTimePushed = System.currentTimeMillis();
    }

    private List<Warden> wardens = new ArrayList<Warden>();

    private class Warden implements PincodeObserver, BarcodeObserver {

        public Warden(PincodeTerminal terminal, ElectronicLock lock) {
            terminal.registerObserver(this);
        }

        public Warden(BarcodeScanner scanner, ElectronicLock lock) {
            scanner.registerObserver(this);
        }

        public void handleCharacter(char c) {
            System.out.println(c);
        }

        public void handleBarcode(String barcode) {
            System.out.println(barcode);
        }

    }

    public void register_and_link(BarcodeScanner scanner, ElectronicLock lock) {
        wardens.add(new Warden(scanner, lock));
    }

    public void register_and_link(PincodeTerminal terminal, ElectronicLock lock) {
        wardens.add(new Warden(terminal, lock));
    }

    public void handleCharacter(char c) {

        long currentTime = System.currentTimeMillis();
        long delay = currentTime - lastTimePushed;

        lastTimePushed = currentTime;

        if (c == '#') {

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
                commonError();
            }

            Ok();
            Unlock();

        } else if (c == '*') {

            // Reset input.
            currentQueue = new StringBuilder();

        } else {
            currentQueue.append(c);
        }
    }

    PincodeTerminal terminal;
    ElectronicLock lock;

    private void Ok() {
        Signal ok = new Signal(terminal, GREEN, 20*1000, 0, 1);
        Thread t = new Thread(ok);
        t.start();
    }

    private void severeError() {
        Signal errorCommon = new Signal(terminal, RED, 1000, 1000, 9);
        Thread t = new Thread(errorCommon);
        t.start();
    }

    private void commonError() {
        Signal errorCommon = new Signal(terminal, RED, 1000, 1000, 3);
        Thread t = new Thread(errorCommon);
        t.start();
    }

    private void Unlock() {
        this.lock.open(20);
    }

    private class Signal implements Runnable {

        private PincodeTerminal terminal;
        private long delay;
        private int color;
        private int flashes = 0;
        private int done = 0;
        private int duration = 0;

        public Signal(PincodeTerminal terminal, int color, int duration, long delay, int flashes) {
            this.terminal = terminal;
            this.color = color;
            this.delay = delay;
            this.flashes = flashes;
            this.duration = duration;
        }

        public void run() {
            if (running_signal != null) {
                return;
            } else {
                running_signal = this;
            }
            try {
                while (done < flashes) {
                    terminal.lightLED(color, duration/1000);
                    done++;
                    Thread.sleep(delay+duration);
                }
            } catch (InterruptedException e) {
                return;
            } finally {
                running_signal = null;
            }
        }
    }
}
