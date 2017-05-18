package etsa02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * This class represents the data composing a BikeOwner inside of the Easy Parks
 * software stack.
 *
 * @author AUTHOR-TAG
 * @version 0.9
 * @since 0.1
 */
public class BikeOwner implements ListElement, Comparable {

    public static int NUM_ARGS = 6;
    private String[] fields = new String[NUM_ARGS];

    private List<Barcode> barcodes;

    /**
     * Create a new BikeOwner instance.
     *
     * @param args 6 consecutive strings; pin, name, ssn, address, phone and email.
     */
    public BikeOwner(String... args) {

        int i = 0;
        int argc = args.length;
        for (; i<argc && i<NUM_ARGS; i++) {
            this.fields[i] = args[i].trim();
        }
        // Not enough parameters, default to empty string.
        for (; i<NUM_ARGS; i++) {
            this.fields[i] = "";
        }

        barcodes = new ArrayList<Barcode>();
        if (argc == NUM_ARGS+1) { // Barcodes supplied.
            String[] tokens = args[NUM_ARGS].split(Barcode.delimiter);
            for (String code : Arrays.asList(tokens)) {
                barcodes.add(new Barcode(code));
            }
        }
    }

    /**
     * @return users pin.
     */
    protected String pin() {return this.fields[0];}
    /**
     * @return users name.
     */
    public String name() {return this.fields[1];}
    /**
     * @return users ssn.
     */
    public String ssn() {return this.fields[2];}
    /**
     * @return users address.
     */
    public String address() {return this.fields[3];}
    /**
     * @return users phone.
     */
    public String phone() {return this.fields[4];}
    /**
     * @return users email.
     */
    public String email() {return this.fields[5];}

    /**
     * @return users as String.
     */
    public String toString() {
        return ssn()+" : "+name()+" : "+pin();
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
        return name().toLowerCase().compareTo(other.name().toLowerCase());
    }

    /**
     * @return List with copy of all Barcode instances.
     */
    public List<Barcode> getBarcodes() {
        return new ArrayList(barcodes);
    }

    /**
     * @param pin String that represents new pin.
     */
    public void setPin(String pin) {
        fields[0] = pin;
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


    /**
     * @return copy of fields.
     */
    protected String[] getFields() {
        String[] fields_return = new String[fields.length];
        System.arraycopy(fields, 0, fields_return, 0, fields.length);
        return fields_return;
    }

    /**
     * @param other Update a BikeOwner with the fields of another BikeOwner.
     */
    protected void update(BikeOwner other) {
        this.fields = other.getFields();
    }

}
