package be.ugent.zeus.hydra.caching;

/**
 * Exception thrown when the cache could not be read or saved.
 *
 * @author Niko Strijbol
 */
class CacheException extends Exception {

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable throwable) {
        super(throwable);
    }
}