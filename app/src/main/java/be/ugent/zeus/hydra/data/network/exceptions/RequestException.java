package be.ugent.zeus.hydra.data.network.exceptions;

/**
 * General exception thrown by a {@link be.ugent.zeus.hydra.data.network.Request}.
 *
 * @author Niko Strijbol
 */
public class RequestException extends Exception {

    public RequestException() {
        super();
    }

    public RequestException(Throwable cause) {
        super(cause);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(String message) {
        super(message);
    }

}
