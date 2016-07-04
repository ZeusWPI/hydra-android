package be.ugent.zeus.hydra.loader.cache.exceptions;

/**
 * @author Niko Strijbol
 * @version 1/06/2016
 */
public class RequestFailureException extends CacheException {

    public RequestFailureException() {
        super();
    }

    public RequestFailureException(Throwable cause) {
        super(cause);
    }
}
