package be.ugent.zeus.hydra.caching;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.requests.common.Request;

/**
 * Encapsulates the data retrieval from a {@link Request} for a {@link Cache}.
 *
 * If data retrieval is necessary, the only method that is guaranteed to be called is the
 * {@link #performRequest()} method. There is no guarantee any of the other methods will be called.
 *
 * @param <D> Type of data that is cached.
 *
 * @author Niko Strijbol
 */
public interface CacheRequest<D> extends Request<D> {

    /**
     * @return The key under which the result will be stored by the {@link Cache}.
     */
    @NonNull
    String getCacheKey();

    /**
     * @return The maximal duration the data should be cached.
     */
    long getCacheDuration();
}