package etsa02;

/**
 * General implementation custom CoreExceptions extend from.
 *
 * @author AUTHOR-TAG
 * @version 1.0
 * @since 0.9
 */
public class ExceptionCoreError extends Exception {

    private String message;

    public ExceptionCoreError(String message) {
        this.message = message;
    }

    public ExceptionCoreError() {
        this.message = "";
    }

    public String toString() {
        return this.message;
    }

};
