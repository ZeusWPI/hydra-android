package be.ugent.zeus.hydra.common.request;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java8.util.function.Function;

/**
 * The basis interface for a request. A request is something that returns data.
 * It is similar for {@link java.util.function.Supplier} in Java 8.
 *
 * The interface does not specify any restrictions or requirements, but many implementations should only be run on
 * a background thread.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface Request<T> {

    /**
     * Perform the request. This may be called multiple times.
     *
     * @param args The args for this request. Can be {@link Bundle#EMPTY}.
     *
     * @return The data.
     */
    @NonNull
    Result<T> performRequest(@NonNull Bundle args);

    /**
     * Identical to {@link #performRequest(Bundle)}, but with {@link Bundle#EMPTY} as argument.
     *
     * @see #performRequest(Bundle)
     */
    @NonNull
    default Result<T> performRequest() {
        return performRequest(Bundle.EMPTY);
    }

    /**
     * This is similar to {@link Result#map(Function)}, but this method allows transforming the request's result
     * without executing the request now.
     *
     * Unless otherwise specified, the resulting request can use this request and inherits this request's properties.
     * For example, if this request is cached, the resulting request will be cached.
     *
     * @param function The function to apply to the result of the original request.
     *
     * @param <R> The type of the resulting request's result.
     *
     * @return The new request.
     */
    default <R> Request<R> map(Function<T, R> function) {
        return args -> performRequest(args).map(function);
    }
}