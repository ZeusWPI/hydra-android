package be.ugent.zeus.hydra.caching;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.threeten.bp.Duration;

import java.io.File;
import java.io.Serializable;

/**
 * A generic cache that the serializes and deserializes data to disk. If not supplied, the
 * cache will use {@link SerializableExecutor} as a default.
 *
 * @author Niko Strijbol
 */
class GenericCache implements Cache {

    private static final String TAG = "GenericCache";

    private final CacheExecutor executor;
    private final File directory;

    GenericCache(Context context) {
        this.directory = context.getCacheDir();
        this.executor = new SerializableExecutor(this.directory);
    }

    GenericCache(File directory, CacheExecutor executor) {
        this.directory = directory;
        this.executor = executor;
    }

    File getDirectory() {
        return directory;
    }

    @Override
    public boolean isExpired(String key, long duration) {
        CacheObject<?> cacheObject = readOrNull(key);
        return shouldRefresh(cacheObject, duration);
    }

    @Override
    public boolean evict(String key) {
        File file = new File(directory, key);
        return !file.isFile() || file.delete();
    }

    @NonNull
    @Override
    public <D extends Serializable> D get(CacheableRequest<D> request, long duration) throws RequestFailureException {
        //Else we do the caching.
        CacheObject<D> object = readOrNull(request.getCacheKey());
        D data;

        if (shouldRefresh(object, duration)) {
            Log.i(TAG, "New response for request " + request.getCacheKey());
            data = request.performRequest();
            object = new CacheObject<>(data);
            try {
                executor.save(request.getCacheKey(), object);
            } catch (CacheException e) {
                Log.w(TAG, "Could not cache request " + request.getCacheKey(), e);
            }
        } else {
            Log.i(TAG, "Cached response for request" + request);
            assert object != null;
            data = object.getData();
        }

        return data;
    }

    @NonNull
    @Override
    public <R extends Serializable> R get(CacheableRequest<R> request) throws RequestFailureException {
        return get(request, request.getCacheDuration());
    }

    @Nullable
    @Override
    public <R extends Serializable> R getOrNull(CacheableRequest<R> request, long duration) {
        try {
            return get(request, duration);
        } catch (RequestFailureException e) {
            Log.w(TAG, "Could not get cache for " + request.getCacheKey(), e);
            return null;
        }
    }

    @Nullable
    @Override
    public <R extends Serializable> R getOrNull(CacheableRequest<R> request) {
        return getOrNull(request, request.getCacheDuration());
    }

    @Nullable
    private <D extends Serializable> CacheObject<D> readOrNull(String key) {
        try {
            return executor.read(key);
        } catch (CacheException e) {
            Log.i(TAG, "Could not read cache for " + key, e);
            return null;
        }
    }

    /**
     * @return True if fresh data should be used, for various reasons.
     */
    @VisibleForTesting
    boolean shouldRefresh(CacheObject<?> object, long duration) {
        return object == null || (duration != ALWAYS //The cache never expires.
                && (duration == Cache.NEVER  //Never cache
                || object.isExpired(Duration.ofMillis(duration)) //Expired cache
                || object.getVersion() != BuildConfig.VERSION_CODE)); //Old cache version
    }
}