package javadoc;

import hardware_interfaces.PincodeObserver;
import hardware_interfaces.BarcodeObserver;
import hardware_interfaces.BarcodePrinter;
import hardware_interfaces.BarcodeScanner;
import hardware_interfaces.ElectronicLock;
import hardware_interfaces.PincodeTerminal;

public class HW implements HWAPI {

    private Core core;
    private ElectronicLock lock;
    private PincodeTerminal terminal;

    private Signal errorCommon;

    private int RED = PincodeTerminal.RED_LED;
    private int GREEN = PincodeTerminal.GREEN_LED;

    private long lastTimePushed;

    private StringBuilder currentQueue = new StringBuilder();

    private Signal running_signal = null;

    public HW(Core core) {
        this.core = core;
        lastTimePushed = System.currentTimeMillis();
    }

    public void addLock(ElectronicLock lock) {
        this.lock = lock;
    }

    public void addTerminal(PincodeTerminal terminal) {
        this.terminal = terminal;
    }

    public void handleBarcode(String string) {
        if (string.equals("g")) {
            terminal.lightLED(PincodeTerminal.GREEN_LED, 3);
        }
        System.out.println(string);
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
            System.out.println(current_pin);

            commonError();

        } else {
            currentQueue.append(c);
        }
    }

    private void commonError() {
        Signal errorCommon = new Signal(terminal, RED, 1000, 3);
        Thread t = new Thread(errorCommon);
        t.start();
    }

    private class Signal implements Runnable {

        private PincodeTerminal terminal;
        private long delay;
        private int color;
        private int flashes = 0;
        private int done = 0;

        public Signal(PincodeTerminal terminal, int color, long delay, int flashes) {
            this.terminal = terminal;
            this.color = color;
            this.delay = delay;
            this.flashes = flashes;
        }

        public void run() {
            if (running_signal != null) {
                return;
            } else {
                running_signal = this;
            }
            try {
                while (done < flashes) {
                    terminal.lightLED(color, 1);
                    done++;
                    Thread.sleep(delay+1000);
                }
            } catch (InterruptedException e) {
                return;
            } finally {
                running_signal = null;
            }
        }
    }
}
