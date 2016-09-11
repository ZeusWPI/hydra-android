package be.ugent.zeus.hydra.requests.common;

/**
 * Exception thrown when the rest template could not be made.
 *
 * @author Niko Strijbol
 */
public class RestTemplateException extends Exception {

    public RestTemplateException() {
        super();
    }

    public RestTemplateException(Throwable cause) {
        super(cause);
    }
}
