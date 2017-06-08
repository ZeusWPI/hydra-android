package be.ugent.zeus.hydra.data.network.requests;

import android.content.Context;
import android.os.Bundle;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.RequestFunction;
import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.network.caching.CacheManager;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.data.network.exceptions.IOFailureException;
import be.ugent.zeus.hydra.data.network.exceptions.PartialDataException;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
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
    public static <R, O> Request<R> map(Request<O> request, Function<O, R> function) {
        return mapE(request, function::apply);
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
    public static <R, O> Request<R> mapE(Request<O> request, RequestFunction<O, R> function) {
        return args -> {
            try {
                return function.apply(request.performRequest(args));
            } catch (PartialDataException e) {
                throw new PartialDataException(e.getCause(), (Serializable) function.apply(e.getData()));
            }
        };
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
            Bundle shouldRefresh = new Bundle();
            Cache cache = CacheManager.defaultCache(context);
            R data;

            if (args != null) {
                shouldRefresh.putAll(args);
            }

            try {
                if (shouldRefresh.getBoolean(RefreshBroadcast.REFRESH_COLD, false)) {
                    data = cache.get(request, args, Cache.NEVER);
                } else {
                    data = cache.get(request, args);
                }
            } catch (IOFailureException e) {
                // TODO: check if we are actually connected to a network or not.
                data = cache.get(request, args, Cache.ALWAYS);
                throw new PartialDataException(e, data);
            }

            return data;
        };
    }
}