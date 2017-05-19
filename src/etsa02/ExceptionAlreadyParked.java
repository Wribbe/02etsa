package etsa02;

/**
 * Custom exception signaling that a bike is in the garage on parking.
 *
 * @author AUTHOR-TAG
 * @version 1.0
 * @since 0.9
 */
public class ExceptionAlreadyParked extends ExceptionCoreError {
    public ExceptionAlreadyParked(String message) {
        super(message);
    }

    public ExceptionAlreadyParked() {
        super();
    }
}
