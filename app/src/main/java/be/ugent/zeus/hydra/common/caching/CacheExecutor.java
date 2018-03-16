package be.ugent.zeus.hydra.common.caching;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * This interface defines the part of the {@link GenericCache} that will serialize the data and save it to disk.
 *
 * @author Niko Strijbol
 */
@Deprecated
public interface CacheExecutor {

    /**
     * Save the data to disk. After calling this method, the data should be cached in such a way that it can be read
     * again by the same CacheExecutor implementation.
     *
     * @param key The key for the cache.
     * @param data The data to cache.

     * @throws CacheException If the data could not be saved.
     */
    <D extends Serializable> void save(String key, CacheObject<D> data) throws CacheException;

    /**
     * Read data from disk.
     *
     * @param key The key for the cache.
     *
     * @return The data, read from disk.
     *
     * @throws CacheException If there is no data or the data could not be read.
     */
    @NonNull
    <D extends Serializable> CacheObject<D> read(String key) throws CacheException;
}
