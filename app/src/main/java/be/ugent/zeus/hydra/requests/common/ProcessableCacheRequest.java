package be.ugent.zeus.hydra.requests.common;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.caching.CacheManager;
import be.ugent.zeus.hydra.caching.CacheRequest;

import java.io.Serializable;

/**
 * A request that takes a cache requests, gets the cached data, and then transforms that data.
 *
 * @author Niko Strijbol
 */
public abstract class ProcessableCacheRequest<D extends Serializable, R> implements Request<R> {

    private final Context context;
    private final CacheRequest<D> cacheRequest;
    private boolean shouldRefresh;

    public ProcessableCacheRequest(Context context, CacheRequest<D> request) {
        this(context, request, false);
    }

    public ProcessableCacheRequest(Context context, CacheRequest<D> request, boolean shouldRefresh) {
        this.context = context;
        this.cacheRequest = request;
        this.shouldRefresh = shouldRefresh;
    }

    /**
     * If this is set to true, the next call for data will force the cache request to use new data. After this call for
     * data, the refresh flag is set to false automatically.
     *
     * @param shouldRefresh Should the next request for data be fresh.
     */
    public void setShouldRefresh(boolean shouldRefresh) {
        this.shouldRefresh = shouldRefresh;
    }

    @NonNull
    @Override
    public R performRequest() throws RequestFailureException {
        Cache cache = CacheManager.defaultCache(context);
        D data;

        if(shouldRefresh) {
            data = cache.get(cacheRequest, Cache.NEVER);
            shouldRefresh = false;
        } else {
            data = cache.get(cacheRequest);
        }

        return transform(data);
    }

    /**
     * Convert the cached data to something else.
     *
     * @param data The cached data.
     *
     * @return Something else.
     */
    @NonNull
    protected abstract R transform(@NonNull D data);
}
