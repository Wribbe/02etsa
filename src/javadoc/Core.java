package javadoc;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Class that holds the core functionality of the Easy Park software stack.
 *
 * @author AUTHOR-TAG
 * @version 0.9
 * @since 0.1
 */
public class Core implements GUIAPI {

    /**
     * Create a new Core instance.
     */

    private Map<String,BikeOwner> db;
    private Random random;

    private final String FULL = "asotaon32;y8aonest";

    private final int size_input_vars = 5;
    private final long barcode_seed = 97234098;
    private final int max_barcodes = 100000;
    private int issued_barcodes = 0;

    private int[] barcode_store = new int[max_barcodes];

    public Core() {
        db = new HashMap<String, BikeOwner>();

        for (int i=0; i<max_barcodes; i++) {
            barcode_store[i] = i;
        }

        random = new Random(barcode_seed);

        // Assign all possible barcodes.
        for (int i=0; i<max_barcodes; i++) {
            barcode_store[i] = i;
        }

        // Shuffle based on seed.
        for (int i = max_barcodes-1; i > 0; i--) {
            // Get any element up to this current point, == don't reshuffle
            // wherer we've already been.
            int index = random.nextInt(i+1);
            int temp = barcode_store[index];
            barcode_store[index] = barcode_store[i];
            barcode_store[i] = temp;
        }
    }

    public boolean newBikeOwner(String... values){
        if (values.length < size_input_vars) {
            System.err.println("To few arguments for creating a bike owner.");
            return false;
        }
        BikeOwner new_owner = new BikeOwner(values);
        db.put(new_owner.ssn(), new_owner);
        return true;
    }

    public boolean editBikeOwner(BikeOwner owner) {
        BikeOwner stored = db.get(owner.ssn());
        if (stored == null) {
            System.err.println("No owner found.");
            return false;
        }
        db.put(owner.ssn(), owner);
        return true;
    }

    public boolean removeBikeOwner(BikeOwner owner){
        BikeOwner stored = db.get(owner.ssn());
        if (stored == null) {
            System.err.println("No owner found.");
            return false;
        }
        db.remove(owner.ssn());
        return true;
    }

    public boolean addBarcode(BikeOwner owner, Barcode barcode){
        BikeOwner stored = db.get(owner.ssn());
        if (stored == null) {
            System.err.println("No owner found.");
            return false;
        }
        stored.add_barcode(barcode);
        if (issued_barcodes < max_barcodes) {
            issued_barcodes++;
        }
        return true;
    }

    public boolean removeBarcode(BikeOwner owner, Barcode barcode) {
        BikeOwner stored = db.get(owner.ssn());
        if (stored == null) {
            System.err.println("No owner found.");
            return false;
        }
        return stored.remove_barcode(barcode);
    }

    public int barcodesLeft() {
        return max_barcodes - issued_barcodes - 1;
    }

    public Barcode newBarcode() {
        return new Barcode(barcode_store[issued_barcodes]);
    }

    public List<BikeOwner> listUsers() {
        List list = new ArrayList<BikeOwner>(db.values());
        Collections.sort(list);
        return list;
    }

    protected List<String> list_users_encoded() {
        List<String> users = new ArrayList<String>();
        for (String ssn : db.keySet()) {
            BikeOwner owner = db.get(ssn);
            StringBuilder strb = new StringBuilder();
            for (String field : owner.fields()) {
                strb.append(field);
                strb.append(GUIAPI.delimiter);
            }
            strb.append(owner.barcodes());
            users.add(strb.toString());
        }
        return users;
    }
}
