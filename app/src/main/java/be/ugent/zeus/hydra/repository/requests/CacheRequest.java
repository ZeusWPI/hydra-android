package be.ugent.zeus.hydra.repository.requests;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import be.ugent.zeus.hydra.data.network.caching.CacheManager;
import be.ugent.zeus.hydra.data.network.exceptions.IOFailureException;
import be.ugent.zeus.hydra.repository.Cache;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;

import java.io.Serializable;

/**
 * Caches a request. The use is similar to the reader-related libraries in Java; this class wraps another request.
 *
 * Mapping this request will still only cache the original value, the mapping itself is not cached. This is to prevent
 * conflicts where multiple mapped requests of the same source request will all save different data for the same key.
 * 
 * @author Niko Strijbol
 */
public final class CacheRequest<R extends Serializable> implements Request<R> {
    
    private final CacheableRequest<R> wrapping;
    private final Cache cache;

    private CacheRequest(CacheableRequest<R> wrapping, Context context) {
        this.wrapping = wrapping;
        this.cache = CacheManager.defaultCache(context);
    }

    @VisibleForTesting
    CacheRequest(CacheableRequest<R> wrapping, Cache cache) {
        this.wrapping = wrapping;
        this.cache = cache;
    }

    public static <R extends Serializable> CacheRequest<R> cache(CacheableRequest<R> request, Context context) {
        return new CacheRequest<R>(request, context);
    }

    @NonNull
    @Override
    public Result<R> performRequest(@Nullable Bundle args) {
        Result<R> data;

        boolean shouldRefresh = args != null && args.getBoolean(BaseLiveData.REFRESH_COLD, false);

        if (shouldRefresh) {
            data = cache.get(wrapping, args, Cache.NEVER);
        } else {
            data = cache.get(wrapping, args);
        }

        // If getting data failed because of the network, get the cached data anyway.
        if (data.hasException() && data.getError() instanceof IOFailureException) {
            Log.w("CachedRequest", "Could not get data from network, getting cached data.");
            data = data.updateWith(cache.get(wrapping, args, Cache.ALWAYS));
        }

        return data;
    }
}