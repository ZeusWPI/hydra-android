package be.ugent.zeus.hydra.repository.requests;

import android.content.Context;

import java8.util.function.Function;

import java.io.Serializable;

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
     * @param request The request to apply the function on.
     * @param function The function to apply.
     *
     * @param <R> The type of the result.
     * @param <O> The type of the original request.
     *
     * @return The new request.
     */
    @Deprecated
    public static <O, R> Request<R> map(Request<O> request, Function<O, R> function) {
        return request.map(function);
    }

    /**
     * Similar to {@link #map(Request, Function)}, but allows for exceptions to happen.
     * See also {@link Result#mapError(RequestFunction)}.
     *
     * @param request The request to apply the function on.
     * @param function The function to apply.
     *
     * @param <R> The type of the result.
     * @param <O> The type of the original request.
     *
     * @return The new request.
     */
    @Deprecated
    public static <O, R> Request<R> mapE(Request<O> request, RequestFunction<O, R> function) {
        return request.mapError(function);
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
        return CacheRequest.cache(request, context);
    }
}