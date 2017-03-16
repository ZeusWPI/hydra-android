package be.ugent.zeus.hydra.plugins;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.network.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.plugin.LoaderPlugin;
import be.ugent.zeus.hydra.loaders.plugin.LoaderProvider;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.data.network.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.SimpleCacheRequest;
import java8.util.Optional;
import java8.util.function.BiFunction;
import java8.util.function.Function;

import java.io.Serializable;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RequestPlugin<D> extends LoaderPlugin<D> {

    private static final String TAG = "RequestPlugin2";

    private boolean refreshRequested = false;
    private ProgressBarPlugin progressBarPlugin;

    public RequestPlugin(LoaderProvider<D> provider) {
        super(provider);
    }

    public RequestPlugin(BiFunction<Context, Boolean, Request<D>> provider) {
        super();
        setLoaderProvider((id, args) -> {
            Context context = getHost().getContext();
            return new RequestAsyncTaskLoader<D>(context, provider.apply(context, refreshRequested));
        });
    }

    public RequestPlugin(Function<Boolean, LoaderProvider<D>> loaderSupplier) {
        super();
        setLoaderProvider(loaderSupplier.apply(refreshRequested));
    }

    /**
     * Instantiate a RequestPlugin with a cacheable request, and effectively caching the request.
     *
     * The sole reason this is a static method is because Java's type system is not strong enough to enforce
     * Serializable on the type parameter for one constructor and not the others.
     *
     * @param request The request to use.
     * @param <D>     The type parameter of the request.
     *
     * @return An instance of RequestPlugin.
     */
    public static <D extends Serializable> RequestPlugin<D> cached(CacheableRequest<D> request) {
        return new RequestPlugin<>(
                (BiFunction<Context, Boolean, Request<D>>) (c, b) -> new SimpleCacheRequest<>(c, request, b)
        );
    }

    public RequestPlugin<D> hasProgress() {
        progressBarPlugin = new ProgressBarPlugin();
        addResultListener(progressBarPlugin.getFinishedCallback());
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
        addErrorListener(throwable -> {
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
        Toast.makeText(getHost().getContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        refreshRequested = true;
        restartLoader();
        refreshRequested = false;
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        if (progressBarPlugin != null) {
            plugins.add(progressBarPlugin);
        }
    }
}