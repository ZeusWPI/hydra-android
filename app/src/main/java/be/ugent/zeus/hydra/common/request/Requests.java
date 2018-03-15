package be.ugent.zeus.hydra.common.request;

import android.content.Context;

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
     * Cache a request.
     *
     * Implementation note: we cannot make this a default function on {@link Request}, since it requires
     * {@code R} to be {@link Serializable}.
     *
     * @param context The context.
     * @param request The request.
     * @param <R>     The type of data.
     *
     * @return The new request.
     */
    public static <R extends Serializable> Request<R> cache(Context context, CacheableRequest<R> request) {
        return CacheRequest.cache(request, context);
    }

    /**
     * Shortcut for calling {@link #cache(Context, CacheableRequest)} and then {@link Request#map(Function)}.
     *
     * It will cache a request and then transform the resulting array into a list.
     *
     * Implementation note: we cannot make this a default function on {@link Request}, since it requires
     * {@code R} to be {@link Serializable} and an array.
     *
     * @param context The context.
     * @param request The request.
     * @param <R>     The type of the data.
     *
     * @return The cached and "listified" request.
     */
    public static <R extends Serializable> Request<List<R>> cachedList(Context context, CacheableRequest<R[]> request) {
        return cache(context, request).map(Arrays::asList);
    }
}