package be.ugent.zeus.hydra.plugins;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.LoaderCallback;
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
public class RequestPlugin<D extends Serializable> extends Plugin implements LoaderCallback<D>, LoaderCallback.DataCallbacks<D> {

    private ProgressBarPlugin progressBarPlugin = new ProgressBarPlugin();
    private LoaderPlugin<D> loaderPlugin = new LoaderPlugin<>(this, this, progressBarPlugin);

    private boolean refreshFlag;

    private final RequestProvider<D> provider;
    private final LoaderCallback.DataCallbacks<D> callback;

    public RequestPlugin(LoaderCallback.DataCallbacks<D> callback, CacheableRequest<D> request) {
        this(callback, (c, b) -> new SimpleCacheRequest<>(c, request, b));
    }

    public RequestPlugin(LoaderCallback.DataCallbacks<D> callback, RequestProvider<D> provider) {
        this.provider = provider;
        this.callback = callback;
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        plugins.add(progressBarPlugin);
        plugins.add(loaderPlugin);
        super.onAddPlugins(plugins);
    }

    @Override
    public void receiveData(@NonNull D data) {
        progressBarPlugin.hideProgressBar();
        callback.receiveData(data);
    }

    @Override
    public void receiveError(@NonNull Throwable e) {
        progressBarPlugin.hideProgressBar();
        callback.receiveError(e);
    }

    @Override
    public Loader<ThrowableEither<D>> getLoader() {
        Request<D> request = provider.getRequest(getHost().getContext(), refreshFlag);
        Loader<ThrowableEither<D>> loader = new RequestAsyncTaskLoader<>(request, getHost().getContext());
        refreshFlag = false;
        return loader;
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
}