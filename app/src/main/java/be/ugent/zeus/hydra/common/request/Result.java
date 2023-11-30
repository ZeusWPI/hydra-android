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

import android.util.Pair;
import androidx.annotation.NonNull;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The result of a {@link Request}.
 * <p>
 * <h1>Either-or-both</h1>
 * This class represents an "inclusive or" type, sometimes referred to as an "outer join" type.
 * This means the class holds either a value, an error or both. To prevent wrong usage, a builder is provided, while the
 * constructor is private.
 * <p>
 * Accessing values when there are none, e.g. calling {@link #data()} when there is no data, will result in an
 * exception. Note that implementing monad laws is not the goal of this class.
 * <p>
 * The class supports various methods for working with this, similar to {@link java.util.Optional}.
 * <p>
 * <h1>Status</h1>
 * In addition to data, this class has support for indicating the status of the request result. The
 * status is either done or continuing.
 * <p>
 * Done means that the request is done. Except for external factors, no more results should be produced. This means the
 * UI could display the request as done.
 * <p>
 * Continuing means that this result represents a partial result, and you should expect the request to produce more
 * results.
 * <p>
 * This class is immutable. Instances of this class must be obtained from the {@link Builder} class.
 *
 * @author Niko Strijbol
 */
public class Result<D> {

    private final D data;
    private final RequestException throwable;
    private final boolean done;

    private Result(RequestException exception, D response, boolean done) {
        this.data = response;
        this.throwable = exception;
        this.done = done;
    }

    /**
     * @return True if the request is done or not.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @return True if the result contains data, otherwise false.
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * @return True if the result contains an exception, otherwise false.
     */
    public boolean hasException() {
        return throwable != null;
    }

    /**
     * @return The data.
     * @throws NoSuchElementException If there is no data.
     */
    public D data() {
        if (data == null) {
            throw new NoSuchElementException("The Result does not contain data.");
        }
        return data;
    }

    /**
     * @return The exception.
     * @throws NoSuchElementException If there is no exception.
     */
    public RequestException error() {
        if (throwable == null) {
            throw new NoSuchElementException("The Result does not contain an exception.");
        }
        return throwable;
    }

    /**
     * @return True if the request was executed without error.
     */
    public boolean isWithoutError() {
        return !hasException();
    }

    public Result<D> asCompleted() {
        return new Result<>(throwable, data, true);
    }

    /**
     * Applies a function to the data of the result. The exception and status are not modified.
     * <p>
     * The function to map the data will never receive a null value and must never produce a null value.
     *
     * @param function The function to apply.
     * @param <R>      The result type.
     * @return The result.
     */
    public <R> Result<R> map(Function<D, R> function) {
        R result = null;
        if (data != null) {
            result = Objects.requireNonNull(function.apply(this.data));
        }

        return new Result<>(this.throwable, result, this.done);
    }

    /**
     * Returns the data if there is no exception. If there is an exception, the exception is thrown, regardless if there
     * is data present or not.
     *
     * @return The data.
     * @throws RequestException If the exception is present.
     */
    @SuppressWarnings("UnusedReturnValue")
    public D getOrThrow() throws RequestException {
        if (throwable != null) {
            throw throwable;
        } else {
            return data;
        }
    }

    /**
     * If a value is present, performs the given action with the value, otherwise performs the given empty-based
     * action.
     * <p>
     * This means the {@code action} will be executed if data is available, regardless of the presence of errors.
     *
     * @param action      The action to be performed, if a value is present.
     * @param emptyAction The empty-based action to be performed, if no value is present.
     */
    public void ifPresentOrElse(Consumer<? super D> action, Runnable emptyAction) {
        if (hasData()) {
            action.accept(data);
        } else {
            emptyAction.run();
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other The value to be returned if there is no value present, maybe null.
     * @return The value, if present, otherwise {@code other}.
     */
    public D orElse(D other) {
        return data != null ? data : other;
    }

    public <T> Result<Pair<D, T>> andThen(Result<T> other) {
        if (hasData()) {
            return other.map(t -> new Pair<>(data, t));
        } else {
            return Result.Builder.fromException(throwable);
        }
    }

    public <T> Result<Pair<D, T>> andThen(Function<D, Result<T>> other) {
        if (hasData()) {
            return other.apply(data()).map(t -> new Pair<>(data, t));
        } else {
            return Result.Builder.fromException(throwable);
        }
    }

    /**
     * This will merge this and another Result into one. The function tries to produce a sensible result:
     *
     * <ul>
     *     <li>The status of the {@code update} result is kept.</li>
     *     <li>If one of the results does not have data, the other result's data will be used. </li>
     *     <li>If neither result has data, no data will be used.</li>
     *     <li>If both have data, the data from the {@code update} result is used.</li>
     *     <li>Same for exceptions.</li>
     * </ul>
     *
     * @param update The Result to merge with.
     * @return Updated result.
     */
    public Result<D> updateWith(Result<D> update) {

        RequestException chosenThrowable;
        if (update.throwable != null) {
            chosenThrowable = update.throwable;
        } else {
            chosenThrowable = this.throwable;
        }

        D chosenData;
        if (update.data != null) {
            chosenData = update.data;
        } else {
            chosenData = this.data;
        }

        return new Result<>(chosenThrowable, chosenData, update.done);
    }

    /**
     * A result is equal to another result if the state, data and error are the same.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result<?> result = (Result<?>) o;
        return done == result.done && Objects.equals(data, result.data) && Objects.equals(throwable, result.throwable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, throwable, done);
    }

    /**
     * Builder for a result.
     *
     * @param <D> The type.
     */
    public static class Builder<D> {

        private D data;
        private RequestException error;

        /**
         * Shortcut for a completed result with data.
         *
         * @param data The data. Cannot be null.
         * @param <D>  The type of the data.
         * @return The result.
         */
        public static <D> Result<D> fromData(@NonNull D data) {
            return new Result<>(null, Objects.requireNonNull(data), true);
        }

        /**
         * Shortcut for a completed result with an exception.
         *
         * @param e   The exception. Cannot be null.
         * @param <D> The type of the data if there would have been data.
         * @return The result.
         */
        public static <D> Result<D> fromException(@NonNull RequestException e) {
            return new Result<>(Objects.requireNonNull(e), null, true);
        }

        public Builder<D> withData(@NonNull D data) {
            this.data = data;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder<D> withError(@NonNull RequestException error) {
            this.error = error;
            return this;
        }

        /**
         * Build a completed result.
         *
         * @return The result.
         */
        public Result<D> build() {
            if (data == null && error == null) {
                throw new IllegalStateException("You must specify at least the exception or the data, or even both.");
            }
            return new Result<>(error, data, true);
        }

        /**
         * Build a partial result. This means the status of the result is continuing.
         *
         * @return The result.
         */
        public Result<D> buildPartial() {
            if (data == null && error == null) {
                throw new IllegalStateException("You must specify at least the exception or the data, or even both.");
            }
            return new Result<>(error, data, false);
        }
    }
}
