package be.ugent.zeus.hydra.repository;

import android.support.annotation.NonNull;
import java8.util.function.Function;

/**
 * @author Niko Strijbol
 */
public class Result<D> {

    private final D data;
    private final Throwable throwable;
    private final Status status;

    private Result(Throwable exception, D response, Status status) {
        this.data = response;
        this.throwable = exception;
        this.status = status;
    }

    /**
     * @return True if there is data.
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * @return The status of the result.
     */
    @NonNull
    public Status getStatus() {
        return status;
    }

    /**
     * @return The data.
     * @throws IllegalStateException If there is no data.
     */
    @NonNull
    public D getData() {

        if (data == null) {
            throw new IllegalStateException("There is no data.");
        }

        return data;
    }

    /**
     * @return The exception.
     * @throws IllegalStateException If there is no exception.
     */
    @NonNull
    public Throwable getError() {

        if (throwable == null) {
            throw new IllegalStateException("There is no error.");
        }

        return throwable;
    }

    /**
     * The status of a {@link Result}.
     *
     * @author Niko Strijbol
     */
    public enum Status {
        /**
         * The request is done, and all data is returned. The caller should not indicate that the request is busy.
         */
        DONE,
        /**
         * An error occurred while getting the data. There might be partial or stale data available anyway. If the caller
         * chooses to display this data, it should be indicated that this data might not be up-to-date.
         */
        ERROR(true, false),
        /**
         * Partial data is available, but the request is continuing. The caller should indicate that the request is busy.
         */
        CONTINUING;

        private final boolean supportsError;
        private final boolean requiresData;

        Status() {
            supportsError = false;
            requiresData = true;
        }

        Status(boolean supportsError, boolean requiresData) {
            this.supportsError = supportsError;
            this.requiresData = requiresData;
        }
    }

    public static class Builder<D> {
        private Status status;
        private D data;
        private Throwable error;

        private Builder() {
        }

        public static <D> Builder<D> create() {
            return new Builder<>();
        }

        public Builder<D> withData(D data) {
            this.data = data;
            return this;
        }

        public Builder<D> withError(Throwable error) {
            this.error = error;
            return this;
        }

        public Builder<D> withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Result<D> build() {
            // Do some checks.
            if (status == null) {
                throw new IllegalStateException("You MUST set the status of the result.");
            }
            if (status.supportsError && error == null) {
                throw new IllegalStateException("You MUST set the exception if the status is ERROR.");
            }
            if (!status.supportsError && error != null) {
                throw new IllegalStateException("You CANNOT set an exception when the status is not ERROR");
            }
            if (status.requiresData && data == null) {
                throw new IllegalStateException("You MUST set the data when the status requires it.");
            }

            return new Result<>(error, data, status);
        }
    }

    /**
     * Applies a function to the data of the result, if there is data. The optional exception and status are kept the same.
     *
     * @param function The function to apply.
     *
     * @param <R> The result type.
     *
     * @return The result.
     */
    public <R> Result<R> apply(Function<D, R> function) {

        R data = null;
        if (hasData()) {
            data = function.apply(this.data);
        }

        return new Result<>(this.throwable, data, this.getStatus());
    }
}
