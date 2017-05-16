package javadoc;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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

    private final int size_input_vars = 5;

    public Core() {
        db = new HashMap<String, BikeOwner>();
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
