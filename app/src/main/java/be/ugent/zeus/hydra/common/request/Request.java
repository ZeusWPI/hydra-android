/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.common.request;

import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.function.Function;

/**
 * The basic interface for a request. A request is something that returns data.
 * <p>
 * The interface does not specify any restrictions or requirements, but many
 * implementations should only be run on a background thread.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface Request<T> {

    /**
     * Perform the request. This method is idempotent and thread-safe.
     *
     * @param args The arguments for this request. Can be {@link Bundle#EMPTY}.
     * @return The data.
     */
    @NonNull
    Result<T> execute(@NonNull Bundle args);

    /**
     * Identical to {@link #execute(Bundle)}, but with {@link Bundle#EMPTY} as argument.
     *
     * @see #execute(Bundle)
     */
    @NonNull
    default Result<T> execute() {
        return execute(Bundle.EMPTY);
    }

    /**
     * This is similar to {@link Result#map(Function)}, but this method allows transforming the request's result
     * without executing the request now.
     * <p>
     * Unless otherwise specified, the resulting request can use this request and inherits this request's properties.
     * For example, if this request is cached, the resulting request will be cached.
     *
     * @param function The function to apply to the result of the original request.
     * @param <R>      The type of the resulting request's result.
     * @return The new request.
     */
    @NonNull
    default <R> Request<R> map(@NonNull Function<T, R> function) {
        return args -> execute(args).map(function);
    }

    /**
     * Execute a second request if the first one is successful. This is useful if
     * you only need the data if both requests succeeded.
     * <p>
     * If the first one errors, the produced error is returned, while the second
     * request is not executed. If the second request errors, the error is returned
     * while the result of the first request is discarded. If both succeed, the
     * data is wrapped in a Pair and returned.
     * <p>
     * The new requests inherits the constraints of both requests. For example,
     * if request A cannot be executed in a background thread and request B
     * must be executed on a background thread, the resulting request will not
     * be executable.
     *
     * @param second The second request to execute.
     * @param <S>    The result type of the second request.
     * @return A new combined request.
     */
    @NonNull
    default <S> Request<S> andThen(@NonNull Function<T, Request<S>> second) {
        return args -> execute(args).andThen(v -> second.apply(v).execute(args)).map(p -> p.second);
    }
}