package be.ugent.zeus.hydra.loaders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

/**
 * Class that simplifies and abstracts a lot of the working with loaders in a portable way. While this functionality
 * could be implemented in a super class of Activity or Fragment, there is no way to re-use the code for both, which
 * is way this class exists.
 *
 * @author Niko Strijbol
 */
public class LoaderCallbackHandler<T> implements LoaderManager.LoaderCallbacks<ThrowableEither<T>> {

    private static int LOADER_ID = 0;

    private final LoaderCallback<T> callback;
    private final ProgressbarListener listener;
    private ResetListener resetListener;

    public LoaderCallbackHandler(LoaderCallback<T> callback) {
        this(callback, null);
    }

    public LoaderCallbackHandler(LoaderCallback<T> callback, ProgressbarListener listener) {
        this.callback = callback;
        this.listener = listener;
    }

    @Override
    public Loader<ThrowableEither<T>> onCreateLoader(int id, Bundle args) {
        return callback.getLoader();
    }

    @Override
    public void onLoadFinished(Loader<ThrowableEither<T>> loader, ThrowableEither<T> data) {
        if(data.hasError()) {
            callback.receiveError(data.getError());
        } else {
            callback.receiveData(data.getData());
        }
        if(listener != null) {
            listener.hideProgressBar();
        }
    }

    @Override
    public void onLoaderReset(Loader<ThrowableEither<T>> loader) {
        if(resetListener != null) {
            resetListener.onLoaderReset();
        }
    }

    public void setResetListener(ResetListener listener) {
        this.resetListener = listener;
    }

    /**
     * Start the loader.
     */
    public void startLoader() {
        // Start the data loader.
        callback.getTheLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * Restart the loader.
     */
    public void restartLoader() {
        // Start the data loader.
        callback.getTheLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void destroyLoader() {
        callback.getTheLoaderManager().destroyLoader(LOADER_ID);
    }

    /**
     * Provides a simple interface to implement to work with loaders.
     *
     * @author Niko Strijbol
     */
    public interface LoaderCallback<T> {

        /**
         * You will probably not implement this method yourself. The activity or fragment implementing this interface
         * should already have this method.
         *
         * @return The loader manager.
         */
        LoaderManager getTheLoaderManager();

        /**
         * Receive the data if the request was completed successfully.
         *
         * @param data The data.
         */
        void receiveData(@NonNull T data);

        /**
         * Receive an error if the request failed for some reason.
         *
         * @param e The occurred exception.
         */
        void receiveError(@NonNull Throwable e);

        /**
         * Provide the loader to use.
         *
         * @return The loader to use.
         */
        Loader<ThrowableEither<T>> getLoader();
    }

    public interface ProgressbarListener {
        void hideProgressBar();
        void showProgressBar();
    }

    public interface ResetListener {
        void onLoaderReset();
    }
}