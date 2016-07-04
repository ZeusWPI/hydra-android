package be.ugent.zeus.hydra.loader;

import android.support.annotation.NonNull;

/**
 * A class that contains either a value or an exception. This is returned as result of
 * the {@link CachedAsyncTaskLoader}, to enable passing of errors.
 *
 * While this is conceptually a monad, Android's lack of support for Java 8 necessitates a different implementation.
 * Monad laws are thus not implemented.
 *
 * @param <D> The type of the data this maybe contains.
 *
 * @author Niko Strijbol
 */
public final class ThrowableEither<D> {

    private final D data;
    private final Throwable throwable;

    public ThrowableEither(@NonNull D response) {
        this.data = response;
        this.throwable = null;
    }

    public ThrowableEither(@NonNull Throwable response) {
        this.data = null;
        this.throwable = response;
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
    public Throwable getError() {
        if(throwable == null || data != null) {
            throw new IllegalStateException("There is an error or no data.");
        }

        return throwable;
    }
}