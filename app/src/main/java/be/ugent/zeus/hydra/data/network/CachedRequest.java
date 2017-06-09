package be.ugent.zeus.hydra.data.network;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.data.network.caching.CacheManager;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.Result;

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
 *
 * @deprecated Use {@link be.ugent.zeus.hydra.data.network.requests.Requests#cache(Context, CacheableRequest)} instead.
 */
@Deprecated
public class CachedRequest<R extends Serializable> implements Request<R> {

    protected final Context context;
    private final CacheableRequest<R> cacheableRequest;
    private Bundle shouldRefresh = new Bundle();

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
        this.shouldRefresh.putBoolean(RefreshBroadcast.REFRESH_COLD, shouldRefresh);
    }

    @NonNull
    @Override
    public Result<R> performRequest(@Nullable Bundle args) {

        if (args != null) {
            shouldRefresh.putAll(args);
        }

        return Requests.cache(context, cacheableRequest).performRequest(shouldRefresh);
    }
}