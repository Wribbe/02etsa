package etsa02;

/**
 * Interface to make it possible to nest BikeUsers and Barcodes in graphical
 * interfaces.
 *
 * @author AUTHOR-TAG
 * @version 0.9
 * @since 0.1
 */
public interface ListElement {
    /**
     * Must be possible to display element as string in order to view in
     * interface.
     *
     * @return String representation of implementer.
     */
    public String toString();
}
