package be.ugent.zeus.hydra.loader;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.cache.exceptions.RequestFailureException;

import java.io.Serializable;

/**
 * Helper methods to reduce code duplication, because we need both the system's loader and the app compat one.
 *
 * @author Niko Strijbol
 */
class LoaderHelper {

    /**
     * The core logic of the loader.
     *
     * @see CachedAsyncTaskLoader#loadInBackground()
     */
    static <T extends Serializable, R> ThrowableEither<R> loadInBackground(Cache cache, boolean refresh, CacheRequest<T, R> request) {

        ThrowableEither<R> returnValue;

        try {
            R content;
            if (refresh) {
                //Get new data
                content = cache.get(request, Cache.NEVER);
            } else {
                content = cache.get(request);
            }
            returnValue = new ThrowableEither<>(content);
        } catch (RequestFailureException e) {
            returnValue = new ThrowableEither<>(e);
        }

        return returnValue;
    }
}