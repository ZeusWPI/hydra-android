package be.ugent.zeus.hydra.loaders;

import android.content.Context;
import android.support.v4.content.Loader;

/**
 * Simple interface to provide a {@link Loader}.
 *
 * Once the min SDK is Java 8 (API 24+), we can replace by a generic one.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface LoaderProvider<T> {

    /**
     * Provide a loader. The returned loader must be fresh, in the sense that you cannot rely on the caller of this
     * function to preserve state or make any guarantees to save instances.
     *
     * The returned loader must work as if it were a newly initialized one.
     *
     * @return The loader.
     */
    Loader<ThrowableEither<T>> getLoader(Context context);
}