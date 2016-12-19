package be.ugent.zeus.hydra.requests.exceptions;

/**
 * Exception thrown when the rest template could not be made.
 *
 * @author Niko Strijbol
 */
public class RestTemplateException extends RequestFailureException {

    public RestTemplateException(Throwable cause) {
        super(cause);
    }
}
