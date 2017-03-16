package be.ugent.zeus.hydra.data.network.caching;

/**
 * Exception thrown when the cache could not be read or saved.
 *
 * @author Niko Strijbol
 */
class CacheException extends Exception {

    CacheException(String message) {
        super(message);
    }

    CacheException(Throwable throwable) {
        super(throwable);
    }
}