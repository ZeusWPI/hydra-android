package be.ugent.zeus.hydra.cache.exceptions;

/**
 * Exception thrown when something goes wrong while reading/accessing cache that cannot be ignored.
 *
 * @author Niko Strijbol
 */
public class CacheException extends Exception {

    public CacheException(Throwable throwable) {
        super(throwable);
    }
}