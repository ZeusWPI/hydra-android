package be.ugent.zeus.hydra.common.request;

import android.content.Context;
import android.util.Log;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.caching.CacheManager;
import be.ugent.zeus.hydra.common.network.IOFailureException;
import java8.util.function.Function;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for use with {@link Request}s.
 *
 * @author Niko Strijbol
 */
public class Requests {

    /**
     * Transform a {@code request} to result in another value. This is similar to {@link Result#map(Function)}, but
     * this method allows transforming the request's result without executing the request now.
     *
     * TODO: look how we can support both Function and our custom function at the same time, without additional methods.
     *
     * @param request  The request to apply the function on.
     * @param function The function to apply.
     * @param <R>      The type of the result.
     * @param <O>      The type of the original request.
     *
     * @return The new request.
     */
    public static <O, R> Request<R> map(Request<O> request, Function<O, R> function) {
        return args -> request.performRequest(args).map(function);
    }

    /**
     * Similar to {@link #map(Request, Function)}, but allows for exceptions to happen.
     * See also {@link Result#mapError(RequestFunction)}.
     *
     * @param request  The request to apply the function on.
     * @param function The function to apply.
     * @param <R>      The type of the result.
     * @param <O>      The type of the original request.
     *
     * @return The new request.
     */
    public static <O, R> Request<R> mapE(Request<O> request, RequestFunction<O, R> function) {
        return args -> request.performRequest(args).mapError(function);
    }

    /**
     * Cache a request.
     *
     * @param context The context.
     * @param request The request.
     * @param <R>     The type of data.
     *
     * @return The new request.
     */
    public static <R extends Serializable> Request<R> cache(Context context, CacheableRequest<R> request) {
        return args -> {
            Cache cache = CacheManager.defaultCache(context);
            Result<R> data;

            boolean shouldRefresh = args != null && args.getBoolean(BaseLiveData.REFRESH_COLD, false);

            if (shouldRefresh) {
                data = cache.get(request, args, Cache.NEVER);
            } else {
                data = cache.get(request, args);
            }

            // If getting data failed because of the network, get the cached data anyway.
            if (data.hasException() && data.getError() instanceof IOFailureException) {
                Log.w("CachedRequest", "Could not get data from network, getting cached data.");
                data = data.updateWith(cache.get(request, args, Cache.ALWAYS));
            }

            return data;
        };
    }

    /**
     * Shortcut for calling {@link #cache(Context, CacheableRequest)} and then {@link #map(Request, Function)}.
     * <p>
     * It will cache a request and then transform the resulting array into a list.
     *
     * @param context The context.
     * @param request The request.
     * @param <R>     The type of the data.
     *
     * @return The cached and "listified" request.
     */
    public static <R extends Serializable> Request<List<R>> cachedList(Context context, CacheableRequest<R[]> request) {
        return map(cache(context, request), Arrays::asList);
    }
}