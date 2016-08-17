package be.ugent.zeus.hydra.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.requests.common.RequestFailureException;

import java.io.Serializable;

/**
 * Generic interface for a cache.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("unused")
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
     * @param name     Name of the cache file.
     * @param duration Expiration to check against.
     *
     * @return True if it is expired, false otherwise.
     */
    boolean isExpired(String name, long duration);

    /**
     * Get data from a request.
     *
     * @param request  The request to get data from.
     * @param duration Expiration of the cache.
     * @param <D>      Type of data from the request.
     * @param <R> Result of the request.
     *
     * @return The data
     *
     * @throws RequestFailureException If something goes wrong.
     */
    @NonNull
    <D extends Serializable, R> R get(CacheRequest<D, R> request, long duration) throws RequestFailureException;

    //This will be a default method once android supports it.
    @NonNull
    <D extends Serializable, R> R get(CacheRequest<D, R> request) throws RequestFailureException;

    //This will be a default method once android supports it.

    /**
     * @return The data or null if the request failed.
     *
     * @see #get(CacheRequest)
     */
    @Nullable
    <D extends Serializable, R> R getOrNull(CacheRequest<D, R> request, long duration);

    //This will be a default method once android supports it.
    @Nullable
    <D extends Serializable, R> R getOrNull(CacheRequest<D, R> request);
}