package be.ugent.zeus.hydra.repository;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.network.RequestFunction;
import be.ugent.zeus.hydra.data.network.exceptions.RequestException;
import java8.util.Objects;
import java8.util.function.Consumer;
import java8.util.function.Function;

import java.util.NoSuchElementException;

/**
 * The result of a {@link be.ugent.zeus.hydra.data.network.Request}.
 *
 * <h2>Either-or-both</h2>
 * This class represents an "inclusive or" type, sometimes referred to as an "outer join" type.
 * This means the class holds either a value, an error or both. To prevent wrong usage, a builder is provided, while
 * the constructor is private.
 *
 * Accessing values when there are none, e.g. calling {@link #getData()} when there is no data, will result in an
 * exception. Note that implementing monad laws is not the goal of this class.
 *
 * The class supports various methods for working with this, similar to {@link java.util.Optional}.
 *
 * <h2>Status</h2>
 * In addition to data, this class has support for indicating the status of the request result. The status is either
 * done or continuing.
 *
 * Done means that the request has done. Except for external factors, no more results should be produced. This
 * means the UI could display the request as done.
 *
 * Continuing means that this result represents a partial result, and you should expect the request to produce more
 * results.
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
     * @return If the request is done or not.
     */
    public boolean isDone() {
        return done;
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasException() {
        return throwable != null;
    }

    /**
     * @return The data.
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

    public static class Builder<D> {
        private D data;
        private RequestException error;

        public Builder() {
        }

        public static <D> Builder<D> create() {
            return new Builder<>();
        }

        public Builder<D> withData(@NonNull D data) {
            this.data = data;
            return this;
        }

        public Builder<D> withError(@NonNull RequestException error) {
            this.error = error;
            return this;
        }

        public Result<D> build() {
            if (data == null && error == null) {
                throw new IllegalStateException("You must specify at least the exception or the data, or even both.");
            }
            return new Result<>(error, data, true);
        }

        public Result<D> buildPartial() {
            if (data == null && error == null) {
                throw new IllegalStateException("You must specify at least the exception or the data, or even both.");
            }
            return new Result<>(error, data, false);
        }

        public static <D> Result<D> fromData(@NonNull D data) {
            return new Result<>(null, Objects.requireNonNull(data), true);
        }

        public static <D> Result<D> fromException(@NonNull RequestException e) {
            return new Result<>(Objects.requireNonNull(e), null, true);
        }
    }

    public Result<D> asCompleted() {
        return new Result<>(throwable, data, true);
    }

    /**
     * Applies a function to the data of the result. The exception and status are not modified.
     *
     * The function to map the data will never receive a null value and must never produce a null value. If there is
     * a situation where converting the value might fail, use {@link #applyError(RequestFunction)} instead.
     *
     * @param function The function to apply.
     *
     * @param <R> The result type.
     *
     * @return The result.
     */
    public <R> Result<R> apply(Function<D, R> function) {

        R result = null;
        if (data != null) {
            result = Objects.requireNonNull(function.apply(this.data));
        }

        return new Result<>(this.throwable, result, this.done);
    }

    /**
     * Applies a function to the data of the result, if there is data. If there is no data, the optional exception and
     * the status are kept the same. If there is data and the function can be applied, it is as if {@link #apply(Function)}
     * had been called. If there is data but the function threw an error, the result will be the exception from the function,
     * no data and the status from the original result.
     *
     * @param function The function to apply.
     *
     * @param <R> The result type.
     *
     * @return The result.
     */
    public <R> Result<R> applyError(RequestFunction<D, R> function) {
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
     * Returns the data if there is no exception. If there is an exception, the exception is thrown, regardless if
     * there is data present or not.
     *
     * @return The data.
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
     * If a value is present, performs the given action with the value, otherwise performs the given empty-based action.
     *
     * @param action The action to be performed, if a value is present.
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
}