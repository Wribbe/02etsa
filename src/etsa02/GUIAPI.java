package etsa02;

import java.util.List;
import java.io.IOException;

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
     * @param old_owner BikeOwner to be edited.
     * @param new_owner BikeOwner used to update old.
     * @return boolean signaling successful edit.
     * */
    public boolean editBikeOwner(BikeOwner old_owner, BikeOwner new_owner);

    /**
     * Remove existing BikeOwner.
     * @param owner BikeOwner to be edited.
     * @throws ExceptionCoreError with message why user can't be removed.
     * @return boolean signaling successful removal.
     * */
    public boolean removeBikeOwner(BikeOwner owner) throws ExceptionCoreError;

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
     * @throws ExceptionCoreError with message why barcode can't be removed.
     * @return boolean signaling successful removal.
     * */
    public boolean removeBarcode(BikeOwner owner, Barcode code) throws ExceptionCoreError ;

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

    /**
     * Get pin for user with SSN.
     * @param ssn SSN for user of pin.
     * @throws IOException if user not found.
     * @return String representing pin for user.
     * */
    public String pin(String ssn) throws IOException;

    /**
     * Set pin for user with SSN.
     * @param owner owner that should have new pin.
     * @param pin new pin.
     * */
    public void setPin(BikeOwner owner, String pin);

    /**
     * Return HWAPI instance from core.
     * @return HWAPI instance from core.
     * */
    public HWAPI HW();

    /**
     * Check if a given serial number corresponds to a registered barcode.
     * @return boolean representing if serial is registered or not.
     */
    public boolean barcodeRegistered(String serial);

    /**
     * Try to remove the bike from the garage.
     * @param barcode Barcode instance representing the bike that should leave
     * the garage.
     * @throws ExceptionOwnerNotInGarage if owner is not registered as beeing
     * inside the garage.
     */
    public void unpark(Barcode barcode) throws ExceptionOwnerNotInGarage;

    /**
     * Registers that a user has entered the grarage.
     * @param owner Instance of BikeOwner representing the user.
     * exited.
     */
    public void enter(BikeOwner owner);

    /**
     * Return the number of unoccupied parking spaces.
     * @param barcode Barcode instance of bike that should be parked in garage.
     * @throws ExceptionGarageFull on full garage.
     * @throws ExceptionAlreadyParked if barcode already registered but not
     * exited.
     */
    public void park(Barcode barcode) throws ExceptionGarageFull, ExceptionAlreadyParked;

    /**
     * Return the number of unoccupied parking spaces.
     * @return int value representing space left in garage.
     */
    public int space_left();

    /**
     * Return instance of BikeOwner that has pin
     * @param pin the pin that the user should have.
     * @return BikeOwner that has the given pin.
     * @throws IOException if user with pin was not found.
     */
    public BikeOwner userWithPin(String pin) throws IOException;
}
