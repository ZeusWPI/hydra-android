package be.ugent.zeus.hydra.requests.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.caching.CacheManager;
import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.requests.exceptions.IOFailureException;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

import java.io.Serializable;

/**
 * This request uses the {@link CacheManager#defaultCache(Context)} to retrieve data from the encapsulated
 * {@link CacheableRequest}.
 *
 * If the devices is offline or there is a network error while reading new data, the cached data is returned if at
 * all possible.
 *
 * After data retrieval, the data is transformed using {@link #transform(Serializable)}. The result of the
 * transformation is returned as final data from the request.
 *
 * A simple implementation of this class that uses the identity function (does nothing) on the data is
 * {@link SimpleCacheRequest}.
 *
 * @param <D> The data that will be retrieved from the cache.
 * @param <R> The final result, after processing the data from the cache.
 *
 * @author Niko Strijbol
 */
public abstract class ProcessableCacheRequest<D extends Serializable, R> implements Request<R> {

    private static final String TAG = "ProcCacheRequest";

    protected final Context context;
    private final CacheableRequest<D> cacheableRequest;
    private boolean shouldRefresh;

    /**
     * Create a request.
     *
     * @param context A context. Can be any context, as the application context is taken.
     * @param request The request.
     * @param shouldRefresh Should fresh data be used or not.
     */
    public ProcessableCacheRequest(Context context, CacheableRequest<D> request, boolean shouldRefresh) {
        this.context = context.getApplicationContext();
        this.cacheableRequest = request;
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

        try {
            if (shouldRefresh) {
                data = cache.get(cacheableRequest, Cache.NEVER);
                shouldRefresh = false;
            } else {
                data = cache.get(cacheableRequest);
            }
        } catch (IOFailureException e) {
            Log.i(TAG, "Network error, trying again...", e);
            OfflineBroadcaster.broadcastNetworkError(context);
            data = cache.get(cacheableRequest, Cache.ALWAYS);
        }

        return transform(data);
    }

    /**
     * Convert the cached data to something else. This is called on the same thread as {@link #performRequest()}, which
     * is often the background thread.
     *
     * @param data The cached data.
     *
     * @return Something else.
     */
    @NonNull
    protected abstract R transform(@NonNull D data) throws RequestFailureException;
}