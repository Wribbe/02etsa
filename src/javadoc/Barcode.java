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

    /**
     * Create a new Barcode instance.
     */
    public Barcode() {
    }
    public String toString() {
        return serial;
    }

    public boolean equals(Barcode barcode) {
        return this.serial.equals(barcode.toString());
    }
}
