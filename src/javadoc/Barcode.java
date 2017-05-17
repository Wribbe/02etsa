package javadoc;

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
     */
    public Barcode(int serial) {
        this.serial = String.format("%05d", serial);
    }

    /**
     * Create a new Barcode instance using string.
     */
    public Barcode(String serial) {
        this.serial = serial;
    }

    public String toString() {
        return serial;
    }

    @Override
    public boolean equals(Object o) {
        ListElement barcode = (ListElement) o;
        return this.serial.equals(barcode.toString());
    }
}
