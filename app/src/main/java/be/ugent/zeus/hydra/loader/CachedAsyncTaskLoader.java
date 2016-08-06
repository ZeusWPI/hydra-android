package be.ugent.zeus.hydra.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.cache.file.SerializeCache;

import java.io.Serializable;

/**
 * Cached task loader. The task loader requires a {@link CacheRequest} that will be executed. This loader uses a
 * {@link SerializeCache} to cache the responses.
 *
 * For more information about the implementation of Loaders see the link below for a detailed guide.
 *
 * The loader has the option to ignore the cache. If set, the cache is ignored for the next request only. This request
 * does save the data in the cache. All subsequent requests will honour the cache again. If you want to ignore the
 * cache again, you need to call {@link #setNextRefresh()}.
 *
 * @param <D> The result of the request. This value is cached, and so it must be Serializable.
 *
 * @author Niko Strijbol
 * @see <a href="http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html">Implementing loaders</a>
 */
public class CachedAsyncTaskLoader<D extends Serializable> extends AsyncTaskLoader<ThrowableEither<D>> {

    private CacheRequest<D> request;
    private ThrowableEither<D> data = null;
    private boolean refresh;
    private final Cache cache;

    /**
     * This loader will honour the cache settings of the request.
     *
     * @param request The request to execute.
     * @param context The context.
     */
    public CachedAsyncTaskLoader(CacheRequest<D> request, Context context) {
        this(request, context, false);
    }

    /**
     * This loader has the option to ignore the cache.
     *
     * @param request   The request to execute.
     * @param context   The context.
     * @param freshData If the data should be fresh or maybe cached.
     */
    public CachedAsyncTaskLoader(CacheRequest<D> request, Context context, boolean freshData) {
        super(context);
        this.request = request;
        this.refresh = freshData;
        this.cache = new SerializeCache(context);
    }

    /**
     * Sets the refresh flag. This means the next request will get new data, regardless of the cache.
     */
    public void setNextRefresh() {
        this.refresh = true;
    }

    /**
     * {@inheritDoc}
     *
     * The data is loaded and cached by default.
     *
     * If the refresh flag is set, the existing cache is ignored, a new request is made and the result of that
     * request is saved in the cache.
     *
     * @return The data or the error that occured while getting the data.
     */
    @Override
    public ThrowableEither<D> loadInBackground() {

        //If the request is cancelled.
        if (isLoadInBackgroundCanceled()) {
            throw new OperationCanceledException();
        }

        //Load the data, and set the refresh flag to false.
        ThrowableEither<D> data = LoaderHelper.loadInBackground(cache, refresh, request);
        this.refresh = false;
        return data;
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