package be.ugent.zeus.hydra.loader.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;

import java.io.Serializable;

/**
 * Generic interface for a cache.
 *
 * @author Niko Strijbol
 */
public interface Cache {

    //Duration constants
    long NEVER = -1;
    long ONE_SECOND = 1000;
    long ONE_MINUTE = 60 * ONE_SECOND;
    long ONE_HOUR = 60 * ONE_MINUTE;
    long ONE_DAY = 24 * ONE_HOUR;
    long ONE_WEEK = 7 * ONE_DAY;

    /**
     * Is the given cache expired or not? This reads the file from disk, so if you need it afterwards, you should read
     * and check yourself. It is a bit more efficient than reading the file itself, so if you just need to check (large
     * amount of files), you should use this.
     *
     * @param name Name of the cache file.
     * @param duration Expiration to check against.
     * @return True if it is expired, false otherwise.
     */
    boolean isExpired(String name, long duration);

    /**
     * Get data from a request.
     *
     * @param request The request to get data from.
     * @param <T> Type of data from the request.
     *           @param duration Expiration of the
     * @return The data
     * @throws RequestFailureException
     */
    @NonNull
    <T extends Serializable> T get(CacheRequest<T> request, long duration) throws RequestFailureException;

    //This will be a default method once android supports it.
    @NonNull
    <T extends Serializable> T get(CacheRequest<T> request) throws RequestFailureException;

    //This will be a default method once android supports it.
    /**
     * @see #get(CacheRequest)
     * @return The data or null if the request failed.
     */
    @Nullable
    <T extends Serializable> T getOrNull(CacheRequest<T> request, long duration);

    //This will be a default method once android supports it.
    @Nullable
    <T extends Serializable> T getOrNull(CacheRequest<T> request);
}