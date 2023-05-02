package researchsim.util;

/**
 * Exception thrown when a save file is invalid or contains incorrect data.
 *
 * @ass2
 */
public class BadSaveException extends Exception {
    /**
     * Constructs a BadSaveException with no detail message.
     */
    public BadSaveException() {
        super();
    }

    /**
     * Constructs a BadSaveException that contains a helpful detail
     * message explaining why the exception occurred.
     *
     * @param message detail message
     */
    public BadSaveException(String message) {
        super(message);
    }

    /**
     * Constructs a BadSaveException that contains a helpful detail message explaining
     * why the exception occurred and the underlying cause of the exception.
     *
     * @param message detail message
     * @param cause throwable that caused this exception
     */
    public BadSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a BadSaveException that stores the underlying cause of the exception.
     *
     * @param cause throwable that caused this exception
     */
    public BadSaveException(Throwable cause) {
        super(cause);
    }
}
