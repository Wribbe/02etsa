package etsa02;

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
