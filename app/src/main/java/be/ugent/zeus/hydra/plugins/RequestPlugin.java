package be.ugent.zeus.hydra.plugins;

import android.content.Context;
import android.support.v4.content.Loader;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.DataCallback;
import be.ugent.zeus.hydra.loaders.LoaderProvider;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.common.SimpleCacheRequest;

import java.io.Serializable;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RequestPlugin<D> extends Plugin {

    private final ProgressBarPlugin progressBarPlugin = new ProgressBarPlugin();
    private final LoaderPlugin<D> loaderPlugin;

    private boolean refreshFlag;

    /**
     * Wrap a request in a SimpleCacheRequest. This will enable caching.
     *
     * This function exists because of weaknesses in the Java generics. Ideally, there would be a second constructor,
     * taking a request as an argument and wrapping it for us. However, this would require <code>D</code> to extend
     * Serializable, which we don't want. Then we could not use this with non-cached requests.
     *
     * Ideally:
     * <code>
     *     public <D must be Serializable here> RequestPlugin (DataCallbacks<D> d, CacheableRequest<D> r)
     * </code>
     *
     * There is no way to indicate that D must be serializable for this method only. By wrapping the request with
     * this function, it does work without losing type safety.
     *
     * @param request The request.
     * @param <T> The type.
     * @return The wrapper.
     */
    public static <T extends Serializable> RequestProvider<T> wrap(CacheableRequest<T> request) {
        return (c, b) -> new SimpleCacheRequest<>(c, request, b);
    }

    /**
     * Note: if you need caching for a {@link CacheableRequest}, you can use the function {@link #wrap(CacheableRequest)},
     * which will construct a RequestProvider for a CacheableRequest that utilises caching.
     *
     * @param callback The data callbacks.
     * @param provider The request provider.
     */
    public RequestPlugin(DataCallback<D> callback, RequestProvider<D> provider) {
        this.loaderPlugin = new LoaderPlugin<>(new DefaultCallback(provider), callback, progressBarPlugin);
    }

    public RequestPlugin(DataCallback<D> callback, LoaderProvider<D> provider) {
        this.loaderPlugin = new LoaderPlugin<>(provider, callback, progressBarPlugin);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(progressBarPlugin);
        plugins.add(loaderPlugin);
    }

    /**
     * Set the refresh flag. If set to true, the next request will be set to true,
     * while the flag will be set to false after the next request.
     *
     * @param refreshFlag
     */
    public void setRefreshFlag(boolean refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    public LoaderPlugin<D> getLoaderPlugin() {
        return loaderPlugin;
    }

    public ProgressBarPlugin getProgressBarPlugin() {
        return progressBarPlugin;
    }

    /**
     * Refresh the data.
     */
    public void refresh() {
        Toast.makeText(getHost().getContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        setRefreshFlag(true);
        loaderPlugin.restartLoader();
    }

    @FunctionalInterface
    public interface RequestProvider<D> {
        Request<D> getRequest(Context context, boolean shouldRefresh);
    }

    private class DefaultCallback implements LoaderProvider<D> {

        private final RequestProvider<D> provider;

        private DefaultCallback(RequestProvider<D> provider) {
            this.provider = provider;
        }

        @Override
        public Loader<ThrowableEither<D>> getLoader(Context context) {
            Request<D> request = provider.getRequest(context, refreshFlag);
            Loader<ThrowableEither<D>> loader = new RequestAsyncTaskLoader<>(request, context);
            refreshFlag = false;
            return loader;
        }
    }
}