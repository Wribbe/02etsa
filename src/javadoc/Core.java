package javadoc;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

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


    private final long BARCODE_SEED = 97234098;
    private final int MAX_BARCODES = 100000;
    private final String DATABASE_PATH = "database.txt";
    private final Charset CHARSET = Charset.forName("UTF-8");
    private final int MAX_PARKED = 100;

    private Map<String,BikeOwner> db;
    private Map<String,List<BikeOwner>> pin_lookup;

    private Random random;
    private int[] barcode_store = new int[MAX_BARCODES];
    private Path database_file;
    private String error = null;
    private HWAPI HW;

    private int issued_barcodes = 0;
    private List<Barcode> parked = new ArrayList<Barcode>();
    private List<BikeOwner> inside = new ArrayList<BikeOwner>();

    public Core() {

        SetupBarcodeDatabase();

        database_file = Paths.get(DATABASE_PATH);

        db = new HashMap<String, BikeOwner>();
        pin_lookup = new HashMap<String, List<BikeOwner>>();

        if (Files.exists(database_file) && !Files.isDirectory(database_file)) {
            try {
                Iterator<String> lines = Files.lines(database_file, CHARSET).iterator();
                issued_barcodes = new Integer(lines.next());
                int current_line = 1;
                while(lines.hasNext()) {
                    String line = lines.next();
                    newBikeOwner(line.split(GUIAPI.delimiter));
                    current_line++;
                }
            } catch (IOException e) {
                // Ignore.
            } catch (NumberFormatException e) {
                error = "Issued barcodes number in top of database file cannot be parsed.";
            }
        } else {
            try {
                Files.createFile(database_file);
            } catch (IOException e) {
                // Ignore.
            }
        }
        HW = new HW(this);
    }

    private String regex_pin = "\\d{4}";

    public HWAPI HW() {
        return HW;
    }

    public int space_left() {
        return MAX_PARKED - parked.size();
    }

    public void park(Barcode barcode) throws GarageFullException, AlreadyParkedException {

        boolean already_parked = parked.indexOf(barcode) != -1;
        boolean full = space_left() <= 0;

        if(full) {
            throw new GarageFullException();
        }

        if(already_parked) {
            throw new AlreadyParkedException();
        }

        parked.add(barcode);
    }

    public void enter(BikeOwner owner) {
        boolean notInGarage = inside.indexOf(owner) == -1;
        if (notInGarage) {
            inside.add(owner);
        }
    }

    public void exit(BikeOwner owner) {
        int index = inside.indexOf(owner);
        boolean inGarage = index != -1;
        if (inGarage) {
            inside.remove(owner);
        }
    }

    public void unpark(Barcode barcode) throws OwnerNotInGarageException {
        for (BikeOwner owner : inside) {
            for (Barcode owner_barcode : owner.getBarcodes()) {
                if (owner_barcode.serial().equals(barcode.serial())) {
                    inside.remove(owner);
                    parked.remove(barcode);
                    return;
                }
            }
        }
        throw new OwnerNotInGarageException();
    }

    public boolean newBikeOwner(String... values){
        BikeOwner new_owner;
        if (!values[0].matches(regex_pin) && values.length <= BikeOwner.NUM_ARGS) {
            values[0] = getPin();
        }
        new_owner = new BikeOwner(values);
        db.put(new_owner.ssn(), new_owner);
        // Update pin_lookup.
        List<BikeOwner> same_pin_list = pin_lookup.get(values[0]);
        if (same_pin_list == null) {
            same_pin_list = new ArrayList<BikeOwner>();
            pin_lookup.put(values[0], same_pin_list);
        }
        same_pin_list.add(new_owner);
        save();
        return true;
    }

    public List<BikeOwner> owners_with_pin(String pin) {
        List<BikeOwner> list = pin_lookup.get(pin);
        if (list == null) {
            return new ArrayList<BikeOwner>();
        }
        return list;
    }

    public boolean editBikeOwner(BikeOwner old_owner, BikeOwner new_owner) {
        BikeOwner stored = db.get(old_owner.ssn());
        if (stored == null) {
            System.err.println("No owner found in editBikeOwner.");
            return false;
        }
        // Changed the SSN remove old from database and re-add.
        if (!stored.ssn().equals(new_owner.ssn())) {
            db.remove(stored.ssn());
            stored.update(new_owner);
            db.put(stored.ssn(), stored);
        } else {
            stored.update(new_owner);
        }
        save();
        return true;
    }

    public boolean removeBikeOwner(BikeOwner owner){
        BikeOwner stored = db.get(owner.ssn());
        if (stored == null) {
            System.err.println("No owner found.");
            return false;
        }
        db.remove(owner.ssn());
        save();
        return true;
    }

    public boolean addBarcode(BikeOwner owner, Barcode barcode){
        BikeOwner stored = db.get(owner.ssn());
        if (stored == null) {
            System.err.println("No owner found.");
            return false;
        }
        stored.add_barcode(barcode);
        if (issued_barcodes < MAX_BARCODES) {
            issued_barcodes++;
        }
        save();
        return true;
    }

    public boolean removeBarcode(BikeOwner owner, Barcode barcode) {
        BikeOwner stored = db.get(owner.ssn());
        if (stored == null) {
            System.err.println("No owner found.");
            return false;
        }
        save();
        return stored.remove_barcode(barcode);
    }

    public int barcodesLeft() {
        return MAX_BARCODES - issued_barcodes - 1;
    }

    public Barcode newBarcode() {
        return new Barcode(barcode_store[issued_barcodes]);
    }

    public List<BikeOwner> listUsers() {
        List list = new ArrayList<BikeOwner>(db.values());
        Collections.sort(list);
        return list;
    }

    public String pin(String ssn) throws IOException {
        BikeOwner stored = db.get(ssn);
        if (stored == null) {
            throw new IOException("No user with ssn: "+ssn+" found.");
        }
        return stored.pin();
    }

    public void setPin(BikeOwner owner, String ssn) {
        owner.setPin(ssn);
    }

    private List<String> list_users_encoded() {
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

    private void SetupBarcodeDatabase() {

        for (int i=0; i<MAX_BARCODES; i++) {
            barcode_store[i] = i;
        }

        random = new Random(BARCODE_SEED);

        // Assign all possible barcodes.
        for (int i=0; i<MAX_BARCODES; i++) {
            barcode_store[i] = i;
        }

        // Shuffle based on seed.
        for (int i = MAX_BARCODES-1; i > 0; i--) {
            // Get any element up to this current point, == don't reshuffle
            // wherer we've already been.
            int index = random.nextInt(i+1);
            int temp = barcode_store[index];
            barcode_store[index] = barcode_store[i];
            barcode_store[i] = temp;
        }
    }

    private void save() {
        try {
            Files.deleteIfExists(database_file);
            Files.createFile(database_file);
            List<String> list = new ArrayList<String>();
            list.add(Integer.toString(issued_barcodes));
            list.addAll(list_users_encoded());
            Files.write(database_file, list, CHARSET);
        } catch (IOException e) {
            // Ignore.
        }
    }

    private String getPin() {
        Random random = new Random(System.currentTimeMillis());
        StringBuilder strb = new StringBuilder();
        for (int i=0; i<4; i++) {
            strb.append(random.nextInt(10));
        }
        return strb.toString();
    }
}
