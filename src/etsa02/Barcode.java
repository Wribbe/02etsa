package etsa02;

import java.util.Random;
import java.lang.StringBuilder;

/**
 * This class represents the data composing a Barcode inside of the Easy Parks
 * software stack.
 *
 * @author AUTHOR-TAG
 * @version 0.9
 * @since 0.1
 */
public class Barcode implements ListElement {

    private int num_digits = 5;
    private String serial;

    public static final String delimiter = "::";

    /**
     * Create a new Barcode instance using int.
     * @param serial int input for Barcode creation.
     */
    public Barcode(int serial) {
        this.serial = String.format("%05d", serial);
    }

    /**
     * Create a new Barcode instance using string.
     * @param serial String input for Barcode creation.
     */
    public Barcode(String serial) {
        this.serial = String.format("%05d", new Integer(serial));
    }

    /**
     * Return string representation for Barcode.
     */
    public String toString() {
        return serial;
    }

    /**
     * Return Barcode serial.
     * @return String representation of serial.
     */
    public String serial() {
        return serial;
    }

    @Override
    public boolean equals(Object o) {
        ListElement barcode = (ListElement) o;
        return this.serial.equals(barcode.toString());
    }
}
