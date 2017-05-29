package be.ugent.zeus.hydra.ui.common.plugins;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.common.plugins.loader.LoaderCallback;
import be.ugent.zeus.hydra.ui.common.plugins.loader.LoaderPlugin;
import be.ugent.zeus.hydra.ui.common.loaders.RequestAsyncTaskLoader;
import java8.util.Optional;
import java8.util.function.BiFunction;
import java8.util.function.Function;

import java.util.List;

/**
 * @author Niko Strijbol
 * @deprecated Use {@link be.ugent.zeus.hydra.repository.RequestRepository}.
 */
@Deprecated
public class RequestPlugin<D> extends LoaderPlugin<D> {

    private static final String TAG = "RequestPlugin";

    private boolean refreshOnce;
    private ProgressBarPlugin progressBarPlugin;

    public RequestPlugin(LoaderCallback<D> provider) {
        super(provider);
    }

    public RequestPlugin(BiFunction<Context, Boolean, Request<D>> provider) {
        super();
        setLoaderProvider((args) -> {
            Context context = getHost().getContext();
            return new RequestAsyncTaskLoader<D>(context, provider.apply(context, refreshOnce));
        });
    }

    public RequestPlugin(Function<Boolean, LoaderCallback<D>> loaderSupplier) {
        super();
        setLoaderProvider(loaderSupplier.apply(refreshOnce));
    }

    public RequestPlugin(Request<D> request) {
        super();
        setLoaderProvider(a -> RequestAsyncTaskLoader.fromRequest(request).apply(getHost().getContext()));
    }

    public RequestPlugin<D> enableProgress() {
        progressBarPlugin = new ProgressBarPlugin();
        progressBarPlugin.register(this);
        return this;
    }

    public Optional<ProgressBarPlugin> getProgressBarPlugin() {
        return Optional.ofNullable(progressBarPlugin);
    }

    /**
     * Enable the default error handling. What this actually does is not defined.
     *
     * However, currently this shows a SnackBar containing a message and a button to refresh the data.
     *
     * @return The plugin for fluent use.
     */
    public RequestPlugin<D> defaultError() {
        addErrorCallback(throwable -> {
            Context c = getHost().getContext();
            Log.e(TAG, "Error while getting data.", throwable);
            Snackbar.make(getHost().getRoot(), c.getString(R.string.failure), Snackbar.LENGTH_LONG)
                    .setAction(c.getString(R.string.again), v -> refresh())
                    .show();
        });
        return this;
    }

    /**
     * Refresh the data.
     */
    public void refresh() {
        refresh(true);
    }

    public void refresh(boolean message) {
        if (message) {
            Toast.makeText(getHost().getContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        }
        refreshOnce = true;
        restartLoader();
        refreshOnce = false;
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        if (progressBarPlugin != null) {
            plugins.add(progressBarPlugin);
        }
    }
}