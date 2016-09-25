package be.ugent.zeus.hydra.requests.common;

/**
 * Exception thrown by a {@link Request} when something goes wrong while producing the data.
 *
 * @author Niko Strijbol
 */
public class RequestFailureException extends Exception {

    public RequestFailureException() {
        super();
    }

    public RequestFailureException(Throwable cause) {
        super(cause);
    }
}