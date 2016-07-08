package be.ugent.zeus.hydra.loader.cache.exceptions;

/**
 * @author Niko Strijbol
 * @version 1/06/2016
 */
public class CacheReadException extends CacheException {

    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     */
    public CacheReadException() {
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the specified cause.
     *
     * @param throwable the cause of this exception.
     */
    public CacheReadException(Throwable throwable) {
        super(throwable);
    }
}
