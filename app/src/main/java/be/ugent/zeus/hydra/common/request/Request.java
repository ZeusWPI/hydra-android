package be.ugent.zeus.hydra.common.request;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java8.util.function.Function;

/**
 * The basis interface for a request. A request is something that returns data, not unlike a AsyncTask, but without
 * any constraints. It is basically a replacement for Supplier<T> in Java 8.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface Request<T> {

    /**
     * Perform the request.
     *
     * @param args The args for this request. This value is nullable because {@link Intent#getExtras()} is nullable.
     *
     * @return The data.
     */
    @NonNull
    Result<T> performRequest(@Nullable Bundle args);

    /**
     * This is similar to {@link Result#map(Function)}, but this method allows transforming the request's result
     * without executing the request now.
     *
     * @param function The function to apply.
     *
     * @param <R> The type of the result.
     *
     * @return The new request.
     */
    default <R> Request<R> map(Function<T, R> function) {
        return args -> performRequest(args).map(function);
    }

    /**
     * Similar to {@link #map(Function)}, but allows for exceptions to happen.
     * See also {@link Result#mapError(RequestFunction)}.
     *
     * @param function The function to apply.
     *
     * @param <R> The type of the result.
     *
     * @return The new request.
     */
    default <R> Request<R> mapError(RequestFunction<T, R> function) {
        return args -> performRequest(args).mapError(function);
    }
}