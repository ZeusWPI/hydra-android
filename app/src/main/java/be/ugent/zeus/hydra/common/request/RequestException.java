package be.ugent.zeus.hydra.common.request;

/**
 * Exception thrown by a {@link Request} when something goes wrong while producing the data.
 *
 * @author Niko Strijbol
 */
public class RequestException extends Exception {

    /**
     * No further details are known. You probably want one of the other constructors.
     *
     * @see Exception#Exception()
     */
    public RequestException() {
        super();
    }

    /**
     * @see Exception#Exception(Throwable)
     */
    public RequestException(Throwable cause) {
        super(cause);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see Exception#Exception(String)
     */
    public RequestException(String message) {
        super(message);
    }
}