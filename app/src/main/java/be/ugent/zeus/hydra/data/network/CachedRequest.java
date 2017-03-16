package be.ugent.zeus.hydra.data.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheManager;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.data.network.exceptions.IOFailureException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

import java.io.Serializable;

/**
 * This request uses the {@link CacheManager#defaultCache(Context)} to retrieve data from the encapsulated
 * {@link CacheableRequest}.
 *
 * If the devices is offline or there is a network error while reading new data, the cached data is returned if at
 * all possible.
 *
 * @param <R> The final result, after processing the data from the cache.
 *
 * @author Niko Strijbol
 */
public class CachedRequest<R extends Serializable> implements Request<R> {

    private static final String TAG = "ProcCacheRequest";

    protected final Context context;
    private final CacheableRequest<R> cacheableRequest;
    private boolean shouldRefresh;

    /**
     * Create a request.
     *
     * @param context A context. Can be any context, as the application context is taken.
     * @param request The request.
     * @param shouldRefresh Should fresh data be used or not.
     */
    public CachedRequest(Context context, CacheableRequest<R> request, boolean shouldRefresh) {
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
        R data;

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

        return data;
    }
}