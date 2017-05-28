package be.ugent.zeus.hydra.data.network.caching;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

import java.io.Serializable;

/**
 * A cache. This is a map like data structure that holds keys and objects. While the keys must be strings,
 * no restriction is applied on the objects. The objects are thus heterogeneous.
 * <p>
 * This is a file cache for {@link CacheableRequest}s. This is not a cache for a non-determined amount of keys. Use
 * something like DiskLruCache for that.
 * <p>
 * The cache is not thread safe. You should make sure to not write using the same key from different threads at the
 * same time. Behavior in that scenario is undefined.
 * <p>
 * The cache duration of an object is specified at retrieval. This simplifies forcing evicting the cache.
 * <p>
 * If the cache duration is set to {@link #NEVER}, the request will not be cached at all. The duration of the cache has
 * a millisecond precision.
 * <p>
 * Note: using 0 as duration is undefined behavior. You should in general only use multiples of the available
 * constants.
 *
 * @author Niko Strijbol
 */
public interface Cache {

    /**
     * Special value that indicates this request should not be cached.
     */
    long NEVER = -1;
    long ALWAYS = -2;
    long ONE_SECOND = 1000;
    long ONE_MINUTE = 60 * ONE_SECOND;
    long ONE_HOUR = 60 * ONE_MINUTE;
    long ONE_DAY = 24 * ONE_HOUR;
    long ONE_WEEK = 7 * ONE_DAY;

    /**
     * This method returns {@code true} if the cached value for the given key was saved longer than the given duration
     * ago. When using {@link #NEVER} as duration, this methods always returns true.
     * <p>
     * Keys for which there is no cache will also return true.
     *
     * @param key      The key used to save the cache.
     * @param duration Expiration to check against (in ms).
     *
     * @return True if it is expired, false otherwise.
     */
    boolean isExpired(String key, long duration);

    /**
     * Delete the cache for a given key. For a key that was not cached, this method does nothing.
     *
     * @param key The key of the cache.
     *
     * @return True if the file was deleted (or there was no file).
     */
    boolean evict(String key);

    /**
     * Get the data. Depending on the cache implementation, this might be cached data or fresh data. If new data is
     * acquired from the request, it will be cached.
     *
     * If {@link #NEVER} is used as duration, existing cache will not be used. The new data will still be cached.
     *
     * This method will only throw an exception if no new data could be acquired. All other exceptions, such as errors
     * while accessing the cache will fail silently and cause the existing cache to be invalidated.
     *
     * @param request  The request to get data from.
     * @param args     The arguments for the request.
     * @param duration Expiration of the cache.
     *
     * @return The data, as if provided by the request.
     *
     * @throws RequestFailureException If the retrieval of new data fails.
     */
    @NonNull
    <R extends Serializable> R get(CacheableRequest<R> request, @Nullable Bundle args, long duration) throws RequestFailureException;

    /**
     * Same as the other method, but uses the built-in cache duration of the request.
     *
     * @see #get(CacheableRequest, Bundle, long)
     */
    @NonNull
    <R extends Serializable> R get(CacheableRequest<R> request, @Nullable Bundle args) throws RequestFailureException;
}