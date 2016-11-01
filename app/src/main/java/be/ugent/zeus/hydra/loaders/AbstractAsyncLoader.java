package be.ugent.zeus.hydra.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;

/**
 * Abstract background loader. This loader loads data into a {@link ThrowableEither}, to be able to communicate errors
 * to the requester.
 *
 * Why a class like this is not already in the API is beyond me, as this class is, for the most part, copying code from
 * Google's tutorials.
 *
 * @param <D> The result of the request.
 *
 * @author Niko Strijbol
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

    @Override
    public ThrowableEither<D> loadInBackground() {

        //If the request is cancelled.
        if (isLoadInBackgroundCanceled()) {
            throw new OperationCanceledException();
        }

        try {
            return new ThrowableEither<>(getData());
        } catch (LoaderException e) {
            //If there is a cause (which should always be the case), give back the cause,
            //and not the loader exception.
            if(e.getCause() != null) {
                return new ThrowableEither<>(e.getCause());
            } else {
                return new ThrowableEither<>(e);
            }
        }
    }

    /**
     * Provide the data for the loader.
     *
     * @return The data.
     * @throws LoaderException If the data could not be provided.
     */
    @NonNull
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