package be.ugent.zeus.hydra.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;

/**
 * Abstract background loader.
 *
 * @param <D> The result of the request.
 *
 * @author Niko Strijbol
 * @see <a href="http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html">Implementing loaders</a>
 */
public abstract class AbstractAsyncLoader<D> extends AsyncTaskLoader<ThrowableEither<D>> {

    private ThrowableEither<D> data = null;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context   The context.
     */
    public AbstractAsyncLoader(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     *
     * The data is loaded and cached by default.
     *
     * If the refresh flag is set, the existing cache is ignored, a new request is made and the result of that
     * request is saved in the cache.
     *
     * @return The data or the error that occurred while getting the data.
     */
    @Override
    public ThrowableEither<D> loadInBackground() {

        //If the request is cancelled.
        if (isLoadInBackgroundCanceled()) {
            throw new OperationCanceledException();
        }

        try {
            return new ThrowableEither<>(getData());
        } catch (LoaderException e) {
            return new ThrowableEither<>(e);
        }
    }

    protected abstract D getData() throws LoaderException;

    /**
     * Pass the data to the listener if the loader was not reset and the loader was started.
     */
    @Override
    public void deliverResult(ThrowableEither<D> data) {

        // The Loader has been reset; ignore the result and invalidate the data.
        if (isReset()) {
            return;
        }

        // Set the data in the loader.
        this.data = data;

        // If the Loader is in a started state, deliver the results to the client.
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    /**
     * Handles requests to start the loader.
     */
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

    /**
     * Handles requests to stop the loader.
     */
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

        // Reset the data.
        data = null;
    }
}