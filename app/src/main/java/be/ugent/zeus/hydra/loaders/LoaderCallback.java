package be.ugent.zeus.hydra.loaders;

import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

/**
 * Provides a simple interface to implement to work with loaders.
 *
 * @author Niko Strijbol
 */
public interface LoaderCallback<T> {

    /**
     * Provide the loader to use.
     *
     * @return The loader to use.
     */
    Loader<ThrowableEither<T>> getLoader();

    interface DataCallbacks<D> {
        /**
         * Receive the data if the request was completed successfully.
         *
         * @param data The data.
         */
        void receiveData(@NonNull D data);

        /**
         * Receive an error if the request failed for some reason.
         *
         * @param e The occurred exception.
         */
        void receiveError(@NonNull Throwable e);
    }
}
