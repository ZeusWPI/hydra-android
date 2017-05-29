package be.ugent.zeus.hydra.data.network.exceptions;

import be.ugent.zeus.hydra.data.network.Request;

/**
 * Exception thrown by a {@link Request} when something goes wrong while producing the data, and no data could
 * be produced. If some data is available despite the exception, use {@link PartialDataException}.
 *
 * @author Niko Strijbol
 */
public class RequestFailureException extends RequestException {

    public RequestFailureException() {
        super();
    }

    public RequestFailureException(Throwable cause) {
        super(cause);
    }

    public RequestFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestFailureException(String message) {
        super(message);
    }
}