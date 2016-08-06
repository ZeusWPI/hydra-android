package be.ugent.zeus.hydra.cache.exceptions;

/**
 * @author Niko Strijbol
 * @version 1/06/2016
 */
public class CacheException extends Exception {
    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     */
    public CacheException() {
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the specified cause.
     *
     * @param throwable the cause of this exception.
     */
    public CacheException(Throwable throwable) {
        super(throwable);
    }
}
