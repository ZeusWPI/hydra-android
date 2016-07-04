package be.ugent.zeus.hydra.fragments.settings.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import be.ugent.zeus.hydra.loader.CachedAsyncTaskLoader;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.loader.cache.Request;

import java.io.Serializable;

/**
 * A special version of the loader using the framework classes for the settings. DO NOT USE OUTSIDE THE SETTINGS.
 *
 * Once we have API 23+, we might switch to this, but not before that.
 *
 * @see CachedAsyncTaskLoader
 *
 * @author Niko Strijbol
 */
public class CachedAsyncTaskLoaderSystem<D extends Serializable> extends AsyncTaskLoader<ThrowableEither<D>> {

    private Request<D> request;

    private ThrowableEither<D> data = null;

    private boolean refresh = false;

    public CachedAsyncTaskLoaderSystem(Request<D> request, Context context) {
        super(context);
        this.request = request;
    }

    /**
     * @param refresh Set the refresh flag.
     */
    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * {@inheritDoc}
     *
     * The data is loaded and cached by default.
     *
     * If the refresh flag is set, the existing cache is ignored, a new request is made and the result of that
     * request is saved in the cache.
     */
    @Override
    public ThrowableEither<D> loadInBackground() {
        // NEEDS API 16 and UP
//        if(isLoadInBackgroundCanceled()) {
//            throw new OperationCanceledException();
//        }

        return CachedAsyncTaskLoader.loadInBackground(getContext(), refresh, request);
    }

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