package be.ugent.zeus.hydra.loader.cache.simple;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.loader.cache.CacheObject;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.loader.cache.exceptions.CacheException;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import org.joda.time.Duration;

import java.io.File;
import java.io.Serializable;

/**
 * Simple cache.
 *
 * This does nothing asynchronously, so use your own threads and such.
 *
 * Note: concurrent access is currently untested.
 *
 * @author Niko Strijbol
 * @version 1/06/2016
 */
public abstract class SimpleCache implements Cache {

    protected static final String TAG = "Hydra SimpleCache";

    protected File directory;
    protected Context context;

    public SimpleCache(Context context, File directory) {
        this.directory = directory;
    }

    /**
     * Use the default cache dir.
     */
    public SimpleCache(Context context) {
        this(context, context.getCacheDir());
    }

    /**
     * Write an object as JSON.
     *
     * @param data The data to write.
     * @throws CacheException
     */
    protected abstract <T extends Serializable> void write(String name, CacheObject<T> data) throws CacheException;

    /**
     * Read data from the cache.
     *
     * @param name Name of the file.
     * @param <T> The type of the CacheObject.
     * @return The CacheObject.
     * @throws CacheException If something went wrong, e.g. the file does not exist.
     */
    @NonNull
    protected abstract <T extends Serializable> CacheObject<T> read(String name) throws CacheException;

    /**
     * Read data from the cache.
     *
     * @see #read(String) The main function.

     * @return The data or null if it does not exist.
     */
    @Nullable
    protected <T extends Serializable> CacheObject<T> readOrNull(String name) {
        try {
            return read(name);
        } catch (CacheException e) {
            return null;
        }
    }

    /**
     * Is the given cache expired or not? This reads the file from disk, so if you need it afterwards, you should read
     * and check yourself. It is a bit more efficient than reading the file itself, so if you just need to check (large
     * amount of files), you should use this.
     *
     * @param name Name of the cache file.
     * @param duration Expiration to check against.
     * @return True if it is expired, false otherwise.
     */
    public boolean isExpired(String name, long duration) {
        CacheObject<?> cacheObject = readOrNull(name);
        return cacheObject == null || duration == Cache.NEVER || cacheObject.isExpired(Duration.millis(duration));
    }

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
    public <T extends Serializable> T get(Request<T> request, long duration) throws RequestFailureException {
        CacheObject<T> object = readOrNull(request.getCacheKey());
        T data;

        if(object == null || duration == Cache.NEVER || object.isExpired(Duration.millis(duration))) {
            Log.d(TAG, "New response for " + request);
            data = request.performRequest();
            object = new CacheObject<>(data);
            try {
                write(request.getCacheKey(), object);
            } catch (CacheException e) {
                Log.w(TAG,  e);
            }
        } else {
            Log.d(TAG, "Cached response for " + request);
            data = object.getData();
        }

        return data;
    }

    @NonNull
    public <T extends Serializable> T get(Request<T> request) throws RequestFailureException {
        return get(request, request.getCacheDuration());
    }

    /**
     * @see #get(Request)
     * @return The data or null if the request failed.
     */
    @Nullable
    public <T extends Serializable> T getOrNull(Request<T> request, long duration) {
        try {
            return get(request, duration);
        } catch (RequestFailureException e) {
            return null;
        }
    }

    @Nullable
    public <T extends Serializable> T getOrNull(Request<T> request) {
        return getOrNull(request, request.getCacheDuration());
    }
}