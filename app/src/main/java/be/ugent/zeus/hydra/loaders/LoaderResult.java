package be.ugent.zeus.hydra.loaders;

import android.support.annotation.NonNull;

/**
 * A result from a {@link AbstractLoader}.
 *
 * This class contains an error OR a value, but not both.
 *
 *
 * A class that contains either a value or an exception. This is returned as result of
 * the {@link AbstractLoader}, to enable passing of errors.
 *
 * Conceptually this can be seen as an Either monad.
 *
 * @param <D> The type of the data it should contain.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("unused")
public final class LoaderResult<D> {

    private final D data;
    private final LoaderException throwable;

    /**
     * Initialize the result with a data value.
     *
     * @param response The data. Must not be null.
     */
    public LoaderResult(@NonNull D response) {
        this.data = response;
        this.throwable = null;
    }

    /**
     * Initialize the result with an error value.
     *
     * @param exception The occurred exception.
     */
    public LoaderResult(@NonNull LoaderException exception) {
        this.data = null;
        this.throwable = exception;
    }

    /**
     * @return True if there is data.
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * @return True if there is an error.
     */
    public boolean hasError() {
        return throwable != null;
    }

    /**
     * @return The data.
     * @throws IllegalStateException If there is no data.
     */
    @NonNull
    public D getData() {
        if(throwable != null || data == null) {
            throw new IllegalStateException("There is an error or no data.");
        }

        return data;
    }

    /**
     * @return The exception.
     * @throws IllegalStateException If there is no exception.
     */
    @NonNull
    public LoaderException getError() {
        if(throwable == null || data != null) {
            throw new IllegalStateException("There is no error.");
        }

        return throwable;
    }
}