package javadoc;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * This class represents the data composing a BikeOwner inside of the Easy Parks
 * software stack.
 *
 * @author AUTHOR-TAG
 * @version 0.9
 * @since 0.1
 */
public class BikeOwner implements ListElement, Comparable {

    private int num_args = 5;
    private String[] fields = new String[num_args];

    private List<Barcode> barcodes;

    /**
     * Create a new BikeOwner instance.
     *
     * @param args 5 consecutive strings; name, ssn, address, phone and email.
     */
    public BikeOwner(String... args) {

        int i = 0;
        int argc = args.length;
        for (; i<argc && i<num_args; i++) {
            this.fields[i] = args[i].trim();
        }
        // Not enough parameters, default to empty string.
        for (; i<num_args; i++) {
            this.fields[i] = "";
        }

        barcodes = new ArrayList<Barcode>();
        if (argc == 6) { // Barcodes supplied.
            String[] tokens = args[5].split(Barcode.delimiter);
            for (String code : Arrays.asList(tokens)) {
                barcodes.add(new Barcode(code));
            }
        }
    }
    /**
     * @return users name.
     */
    public String name() {return this.fields[0];}
    /**
     * @return users ssn.
     */
    public String ssn() {return this.fields[1];}
    /**
     * @return users address.
     */
    public String address() {return this.fields[2];}
    /**
     * @return users phone.
     */
    public String phone() {return this.fields[3];}
    /**
     * @return users email.
     */
    public String email() {return this.fields[4];}

    /**
     * @return users as String.
     */
    public String toString() {
        return name();
    }

    /**
     * @param barcode Barcode to be added.
     */
    public void add_barcode(Barcode barcode) {
        barcodes.add(barcode);
    }

    /**
     * @param barcode Barcode to be removed.
     * @return boolean signaling if barcode was removed.
     */
    public boolean remove_barcode(Barcode barcode) {
        return barcodes.remove(barcode);
    }

    /**
     * @param o Object to be compeered to.
     * @return integer < 0 if this is < then o, == 0 if this == 0 and > 0 if
     * this > 0.
     */
    public int compareTo(Object o) {
        BikeOwner other = (BikeOwner) o;
        return name().compareTo(other.name());
    }

    /**
     * @return List with copy of all Barcode instances.
     */
    public List<Barcode> getBarcodes() {
        return new ArrayList(barcodes);
    }

    /**
     * @return String array containing all the fields of the BikeOwner.
     */
    protected String[] fields() {
        return fields;
    }

    /**
     * @return String with all the barcodes concatenated.
     */
    protected String barcodes() {
        StringBuilder strb = new StringBuilder();
        for (Barcode barcode : barcodes) {
            strb.append(barcode.toString());
            strb.append(Barcode.delimiter);
        }
        if (!barcodes.isEmpty()) {
            // Trim trailing occurrence of delimiter.
            return strb.substring(0, strb.lastIndexOf(Barcode.delimiter));
        } else {
            return strb.toString();
        }
    }
}
