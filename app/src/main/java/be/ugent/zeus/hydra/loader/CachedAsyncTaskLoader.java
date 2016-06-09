package be.ugent.zeus.hydra.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;
import be.ugent.zeus.hydra.loader.cache.Cache;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.cache.file.SerializeCache;

import java.io.Serializable;

/**
 * Cached task loader. The task loader requires a {@link Request} that will be executed.
 *
 * For more information about the implementation of Loaders see the link below for a detailed guide.
 *
 * It is possible for this loader to return null. This can indicate an error or an empty value, depending on the
 * request that is being executed. While not ideal, this is a side effect of the API of Loaders.
 *
 * @param <D> The result of the request. This value is cached, and so it must be Serializable.
 *
 * @see <a href="http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html">Implementing loaders</a>
 *
 * @author Niko Strijbol
 */
public class CachedAsyncTaskLoader<D extends Serializable> extends AsyncTaskLoader<ThrowableEither<D>> {

    private Request<D> request;

    private ThrowableEither<D> data = null;

    private boolean refresh;

    public CachedAsyncTaskLoader(Request<D> request, Context context) {
        this(request, context, false);
    }

    public CachedAsyncTaskLoader(Request<D> request, Context context, boolean freshData) {
        super(context);
        this.request = request;
        this.refresh = freshData;
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
        if(isLoadInBackgroundCanceled()) {
            throw new OperationCanceledException();
        }

        ThrowableEither<D> data = loadInBackground(getContext(), refresh, request);
        this.refresh = false;
        return data;
    }

    /**
     * Helper function for re-use in the system task loader.
     */
    public static <T extends Serializable> ThrowableEither<T> loadInBackground(Context context, boolean refresh, Request<T> request) {
        Cache cache = new SerializeCache(context);

        ThrowableEither<T> returnValue;

        try {
            T content;
            if (refresh) {
                content = cache.get(request, Cache.NEVER);
            } else {
                content = cache.get(request);
            }
            returnValue = new ThrowableEither<>(content);
        } catch (RequestFailureException e) {
            returnValue = new ThrowableEither<>(e);
        }

        return returnValue;
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