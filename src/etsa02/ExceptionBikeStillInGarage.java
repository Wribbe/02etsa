package etsa02;

/**
 * Custom exception signaling that a bike is still parked in garage on removal.
 *
 * @author AUTHOR-TAG
 * @version 1.0
 * @since 0.9
 */
class ExceptionBikeStillInGarage extends ExceptionCoreError {

    public ExceptionBikeStillInGarage(String message) {
        super(message);
    }

    public ExceptionBikeStillInGarage() {
        super();
    }

};
