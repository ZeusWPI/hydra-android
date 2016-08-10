package be.ugent.zeus.hydra.cache;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.requests.common.Request;

import java.io.Serializable;

/**
 * Represents a cacheable request for data. This is the class that provides the data for the {@link Cache}.
 *
 * Note that there is no guarantee for the order in which the methods of this class are called, if called at all.
 *
 * If your cache key or cache duration depends on the result, one solution is the perform the request as soon as
 * one the of the methods is called, and saving the result locally in the class.
 *
 * @author Niko Strijbol
 */
public interface CacheRequest<T extends Serializable, R> extends Request<T> {

    /**
     * @return The key under which the result will be stored by the {@link Cache}.
     */
    @NonNull
    String getCacheKey();

    /**
     * @return The maximal duration the data should be cached.
     */
    long getCacheDuration();

    /**
     * Convert the data T from the cache to the result data R.
     *
     * @param data The data.
     *
     * @return The data.
     */
    @NonNull
    R getData(@NonNull T data);
}