package javadoc;

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
    public Core() {

    }

    public boolean newBikeOwner(String[] values){ return true; }
    public boolean editBikeOwner(BikeOwner owner){ return true; }
    public boolean removeBikeOwner(BikeOwner owner){ return true; }
    public boolean addBarcode(BikeOwner owner){ return true; }
    public boolean removeBarcode(BikeOwner owner, Barcode code){ return true; }
}
