package be.ugent.zeus.hydra.ui.common.loaders;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;

/**
 * Abstract loader that provides the missing pieces from the SDK, and additional functions.
 *
 * The result of the this loader is wrapped in a {@link LoaderResult}, to provide error handling.
 * A side effect of this is that implementors must not implement {@link #loadInBackground()}, but a new method
 * called {@link #loadData()}.
 *
 * This is a asynchronous loader, meaning {@link #loadData()} will not be called on the main thread.
 *
 * @param <D> The result of the request.
 *
 * @author Niko Strijbol
 */
@Deprecated
public abstract class AbstractLoader<D> extends AsyncTaskLoader<LoaderResult<D>> {

    private LoaderResult<D> data = null;

    public AbstractLoader(Context context) {
        super(context);
    }

    @Override
    public LoaderResult<D> loadInBackground() {

        //If the request is cancelled.
        if (isLoadInBackgroundCanceled()) {
            throw new OperationCanceledException();
        }

        try {
            return new LoaderResult<>(loadData());
        } catch (LoaderException e) {
            return new LoaderResult<>(e);
        }
    }

    /**
     * Load the data for use in the loader.
     *
     * The implementation has the same requirements as {@link #loadInBackground()}, e.g. checking if the loader
     * was cancelled.
     *
     * @return The data. Cannot be null.
     *
     * @throws LoaderException The exception if something went wrong.
     */
    @NonNull
    protected abstract D loadData() throws LoaderException;

    @Override
    public void deliverResult(LoaderResult<D> data) {

        //The Loader has been reset; ignore the result and invalidate the data.
        if (isReset()) {
            onReleaseResources();
            return;
        }

        //Set the data in the loader.
        this.data = data;

        // If the Loader is in a started state, deliver the results to the client.
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    public void onCanceled(LoaderResult<D> data) {
        super.onCanceled(data);
        onReleaseResources();
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        // If the data is available, deliver it.
        if (data != null) {
            deliverResult(data);
        }

        // When the observer detects a change, it should call onContentChanged() on the Loader, which will
        // cause the next call to takeContentChanged() to return true. If this is ever the case
        // (or if the current data is null), we force a new load.
        if (takeContentChanged() || data == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();

        // Stop the request.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader has stopped.
        onStopLoading();

        // Reset the data if needed.
        if (data != null) {
            onReleaseResources();
        }
    }

    /**
     * This method is called when the loader must free it's resources.
     *
     * The default implementation releases the data held by this loader by setting it to null. If you need the data
     * after that, it is best to first do what you need.
     *
     * This method is called on the main thread.
     */
    @CallSuper
    @MainThread
    protected void onReleaseResources() {
        this.data = null;
    }
}