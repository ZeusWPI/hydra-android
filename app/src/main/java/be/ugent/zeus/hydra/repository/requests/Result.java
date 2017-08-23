package be.ugent.zeus.hydra.repository.requests;

import android.support.annotation.NonNull;
import java8.util.Objects;
import java8.util.function.Consumer;
import java8.util.function.Function;

import java.util.NoSuchElementException;

/**
 * The result of a {@link Request}.
 *
 * <h2>Either-or-both</h2>
 * This class represents an "inclusive or" type, sometimes referred to as an "outer join" type.
 * This means the class holds either a value, an error or both. To prevent wrong usage, a builder is provided, while the
 * constructor is private.
 *
 * Accessing values when there are none, e.g. calling {@link #getData()} when there is no data, will result in an
 * exception. Note that implementing monad laws is not the goal of this class.
 *
 * The class supports various methods for working with this, similar to {@link java.util.Optional}.
 *
 * <h2>Status</h2>
 * In addition to data, this class has support for indicating the status of the request result. The
 * status is either done or continuing.
 *
 * Done means that the request has done. Except for external factors, no more results should be produced. This means the
 * UI could display the request as done.
 *
 * Continuing means that this result represents a partial result, and you should expect the request to produce more
 * results.
 *
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
     *
     * @throws NoSuchElementException If there is no data.
     */
    public D getData() {
        if (data == null) {
            throw new NoSuchElementException("The Result does not contain data.");
        }
        return data;
    }

    /**
     * @return The exception.
     *
     * @throws NoSuchElementException If there is no exception.
     */
    public RequestException getError() {
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
     * The function to map the data will never receive a null value and must never produce a null value. If there is a
     * situation where converting the value might fail, use {@link #mapError(RequestFunction)} instead.
     *
     * @param function The function to apply.
     * @param <R>      The result type.
     *
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
     * Applies a function to the data of the result, if there is data. If there is no data, the optional exception and
     * the status are kept the same. If there is data and the function can be applied, it is as if {@link
     * #map(Function)} had been called. If there is data but the function threw an error, the result will be the
     * exception from the function, no data and the status from the original result.
     *
     * @param function The function to apply.
     * @param <R>      The result type.
     *
     * @return The result.
     */
    public <R> Result<R> mapError(RequestFunction<D, R> function) {
        try {
            if (data != null) {
                return new Result<>(this.throwable, Objects.requireNonNull(function.apply(data)), this.done);
            } else {
                return new Result<>(this.throwable, null, this.done);
            }
        } catch (RequestException e) {
            return new Result<>(e, null, this.done);
        }
    }

    /**
     * If a value is present, performs the given action with the value, otherwise does nothing.
     *
     * @param action The action to be performed, if a value is present.
     */
    public void ifPresent(Consumer<? super D> action) {
        if (data != null) {
            action.accept(data);
        }
    }

    /**
     * Returns the data if there is no exception. If there is an exception, the exception is thrown, regardless if there
     * is data present or not. See {@link #getDataOrThrow()} to get the data if present.
     *
     * @return The data.
     *
     * @throws RequestException If the exception is present.
     */
    public D getOrThrow() throws RequestException {
        if (throwable != null) {
            throw throwable;
        } else {
            return data;
        }
    }

    /**
     * Returns the data if it is present. If there is no data, the exception is thrown. You might also want to use
     * {@link #getOrThrow()}, which will throw an exception if there is one, regardless if there is data or not.
     *
     * @return The data.
     *
     * @throws RequestException If there is no data.
     */
    public D getDataOrThrow() throws RequestException {
        if (hasData()) {
            return data;
        } else {
            throw throwable;
        }
    }

    /**
     * If a value is present, performs the given action with the value, otherwise performs the given empty-based
     * action.
     *
     * @param action      The action to be performed, if a value is present.
     * @param emptyAction The empty-based action to be performed, if no value is present.
     */
    public void ifPresentOrElse(Consumer<? super D> action, Runnable emptyAction) {
        if (data != null) {
            action.accept(data);
        } else {
            emptyAction.run();
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other The value to be returned if there is no value present, maybe null.
     *
     * @return The value, if present, otherwise {@code other}.
     */
    public D orElse(D other) {
        return data != null ? data : other;
    }

    /**
     * This will merge this and another Result into one. The function tries to produce a sensible result:
     *
     * <ul>
     *     <li>The status of the {@code update} request is kept.</li>
     *     <li>If one of the results do not have data, the other one's data is used. </li>
     *     <li>If neither has data, no data will be used.</li>
     *     <li>If both have data, the data from the {@code update} result is used.</li>
     *     <li>Same for exceptions.</li>
     * </ul>
     *
     * @param update The Result to merge with.
     *
     * @return Updated result.
     */
    public Result<D> updateWith(Result<D> update) {

        RequestException chosenThrowable;
        if (update.throwable != null) {
            chosenThrowable = update.throwable;
        } else if (this.throwable != null) {
            chosenThrowable = this.throwable;
        } else {
            chosenThrowable = null;
        }

        D chosenData;
        if (update.data != null) {
            chosenData = update.data;
        } else if (this.data != null) {
            chosenData = this.data;
        } else {
            chosenData = null;
        }

        return new Result<>(chosenThrowable, chosenData, update.done);
    }

    /**
     * Builder for a result.
     *
     * @param <D> The type.
     */
    public static class Builder<D> {

        private D data;
        private RequestException error;

        public Builder() {
        }

        /**
         * Shortcut for a completed result with data.
         *
         * @param data The data. Cannot be null.
         * @param <D> The type of the data.
         *
         * @return The result.
         */
        public static <D> Result<D> fromData(@NonNull D data) {
            return new Result<>(null, Objects.requireNonNull(data), true);
        }

        /**
         * Shortcut for a completed result with an exception.
         *
         * @param e The exception. Cannot be null.
         * @param <D> The type of the data if there would have been data.
         *
         * @return The result.
         */
        public static <D> Result<D> fromException(@NonNull RequestException e) {
            return new Result<>(Objects.requireNonNull(e), null, true);
        }

        public Builder<D> withData(@NonNull D data) {
            this.data = data;
            return this;
        }

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