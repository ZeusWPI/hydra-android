package be.ugent.zeus.hydra.data.network.requests;

import android.content.Context;
import android.util.Log;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.RequestFunction;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheManager;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.data.network.exceptions.IOFailureException;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.Result;
import java8.util.function.BiFunction;
import java8.util.function.Function;

import java.io.Serializable;
import java.util.List;

/**
 * Utility methods for use with {@link Request}s.
 *
 * @author Niko Strijbol
 */
public class Requests {

    @Deprecated
    public static <R> BiFunction<Context, Boolean, Request<List<R>>> cachedArray(CacheableRequest<R[]> request) {
        return (c, b) -> new ListRequest<>(new CachedRequest<>(c, request, b));
    }

    /**
     * Apply a function to a request.
     *
     * TODO: look how we can support both Function and our custom function at the same time, without additional methods.
     *
     * @param request The request to apply the function on.
     * @param function The function to apply.
     *
     * @param <R> The type of the result.
     * @param <O> The type of the original request.
     *
     * @return The new request.
     */
    public static <O, R> Request<R> map(Request<O> request, Function<O, R> function) {
        return args -> request.performRequest(args).apply(function);
    }

    /**
     * Apply a function to a request.
     *
     * @param request The request to apply the function on.
     * @param function The function to apply.
     *
     * @param <R> The type of the result.
     * @param <O> The type of the original request.
     *
     * @return The new request.
     */
    public static <O, R> Request<R> mapE(Request<O> request, RequestFunction<O, R> function) {
        return args -> request.performRequest(args).applyError(function);
    }

    /**
     * Cache a request.
     *
     * @param context The context.
     * @param request The request.
     * @param <R> The type of data.
     * @return The new request.
     */
    public static <R extends Serializable> Request<R> cache(Context context, CacheableRequest<R> request) {
        return args -> {
            Cache cache = CacheManager.defaultCache(context);
            Result<R> data;

            boolean shouldRefresh = args != null && args.getBoolean(RefreshBroadcast.REFRESH_COLD, false);

            if (shouldRefresh) {
                data = cache.get(request, args, Cache.NEVER);
            } else {
                data = cache.get(request, args);
            }

            // If getting data failed because of the network, get the cached data anyway.
            if (data.hasException() && data.getError() instanceof IOFailureException) {
                Log.i("CachedRequest", "Could not get data from network, getting cached data.");
                data = data.updateWith(cache.get(request, args, Cache.ALWAYS));
            }

            return data;
        };
    }
}