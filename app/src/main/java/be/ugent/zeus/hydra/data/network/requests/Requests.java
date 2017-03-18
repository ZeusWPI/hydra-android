package be.ugent.zeus.hydra.data.network.requests;

import android.content.Context;

import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import java8.util.function.BiFunction;

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
}