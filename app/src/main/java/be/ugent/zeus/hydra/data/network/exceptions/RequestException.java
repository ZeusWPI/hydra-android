package be.ugent.zeus.hydra.data.network.exceptions;

import be.ugent.zeus.hydra.data.network.Request;

/**
 * Exception thrown by a {@link Request} when something goes wrong while producing the data, and no data could
 * be produced. If some data is available despite the exception, use {@link PartialDataException}.
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