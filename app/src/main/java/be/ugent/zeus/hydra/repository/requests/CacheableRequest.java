package be.ugent.zeus.hydra.repository.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.repository.Cache;

/**
 * A request that provides additional metadata about the requested data to facilitate caching of said data.
 * This is especially useful when getting data from a remote source, or when calculating the data is very resource
 * intensive.
 *
 * Note that this request is not responsible for the caching itself, this interface merely indicates that the data can
 * be cached. The request should thus behave like a normal {@link Request} and always return fresh data.
 *
 * It is up to the caller to decide which data to use and which not. Refer to the {@link Cache} implementation for
 * details.
 *
 * @param <D> Type of data that is cached.
 *
 * @author Niko Strijbol
 */
public interface CacheableRequest<D> extends Request<D> {

    /**
     * Provide a suggestion to the caller under which name this data can be saved.
     *
     * @return The key under which the result will be stored by the {@link Cache}.
     */
    @NonNull
    String getCacheKey();

    /**
     * Provide a suggestion for the maximal cache duration.
     *
     * @return The maximal duration the data should be cached.
     */
    long getCacheDuration();
}