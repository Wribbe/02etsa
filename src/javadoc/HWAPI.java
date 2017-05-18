package javadoc;

import hardware_interfaces.PincodeObserver;
import hardware_interfaces.BarcodeObserver;
import hardware_interfaces.BarcodePrinter;
import hardware_interfaces.BarcodeScanner;
import hardware_interfaces.ElectronicLock;
import hardware_interfaces.PincodeTerminal;

/**
 * @author AUTHOR-TAG
 * @version 0.9
 * @since   0.1
 */

/**
 * Interface for handling which methods are available for current hardware peripherals.
 * */
public interface HWAPI extends PincodeObserver, BarcodeObserver {

    public void addLock(ElectronicLock lock);

    public void addTerminal(PincodeTerminal terimal);
}
