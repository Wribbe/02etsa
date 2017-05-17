package javadoc;

import java.util.List;

/**
 * @author AUTHOR-TAG
 * @version 0.9
 * @since   0.1
 */

/**
 * Interface for handling which methods are available for a GUI implementation
 * that makes use of the Core class.
 * */
public interface GUIAPI {

    public static final String delimiter = ";;";

    /**
     * Add new BikeOwner to system.
     * @param values BikeOwner values.
     * @return boolean signaling successful addition.
     * @see BikeOwner
     * */
    public boolean newBikeOwner(String... values);

    /**
     * Edit existing BikeOwner.
     * @param owner BikeOwner to be edited.
     * @return boolean signaling successful edit.
     * */
    public boolean editBikeOwner(BikeOwner owner);

    /**
     * Remove existing BikeOwner.
     * @param owner BikeOwner to be edited.
     * @return boolean signaling successful removal.
     * */
    public boolean removeBikeOwner(BikeOwner owner);

    /**
     * Add barcode to existing BikeOwner.
     * @param owner BikeOwner to be edited.
     * @param barcode Barcode to be added.
     * @return boolean signaling successful addition.
     * */
    public boolean addBarcode(BikeOwner owner, Barcode barcode);

    /**
     * Remove barcode from existing BikeOwner.
     * @param owner BikeOwner to get barcode removed.
     * @param code Barcode that should be removed.
     * @return boolean signaling successful removal.
     * */
    public boolean removeBarcode(BikeOwner owner, Barcode code);

    /**
     * List all users currently in the database.
     * @return list containing all BikeOwner object in database.
     * */
    public List<BikeOwner> listUsers();

    /**
     * Get a unique Barcode.
     * @return new unique Barcode instance.
     * */
    public Barcode newBarcode();

    /**
     * Get number of unique barcodes left.
     * @return new unique Barcode instance.
     * */
    public int barcodesLeft();
}
