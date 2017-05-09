package javadoc;

/**
 * @author AUTHOR-TAG
 * @version 0.9
 * @since   0.1
 */

/**
 * Interface for handling which methods are available for current hardware peripherals.
 * */
public interface HWAPI {

    /**
     * Handle received barcode from scanner.
     * @param string String value returned from scanner.
     * @return boolean signaling barcode processed successfully.
     * */
    public boolean handleBarcode(String string);

    /**
     * Handle received character from keypad.
     * @param c returned character from keypad.
     * */
    public void handleCharacter(char c);

    /**
     * Open electronic lock.
     * @param lock the lock that should be opened.
     * @param duration the amount of seconds the lock should be opened.
     * */
    public void open(ElectronicLock lock, int duration);
}

