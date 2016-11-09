package be.ugent.zeus.hydra.plugins;

import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.LoaderCallback;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.plugins.common.Plugin;

import java.io.Serializable;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RequestPlugin<D extends Serializable> extends Plugin implements LoaderCallback<D>, LoaderCallback.DataCallbacks<D> {

    private ProgressBarPlugin progressBarPlugin = new ProgressBarPlugin();
    private LoaderPlugin<D> loaderPlugin = new LoaderPlugin<>(this, this, progressBarPlugin);

    private boolean refreshFlag;

    private final CacheableRequest<D> request;
    private final LoaderCallback.DataCallbacks<D> callback;

    public RequestPlugin(LoaderCallback.DataCallbacks<D> callback, CacheableRequest<D> request) {
        this.request = request;
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
        Loader<ThrowableEither<D>> loader = RequestAsyncTaskLoader.getSimpleLoader(getHost().getContext(), request, refreshFlag);
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
}