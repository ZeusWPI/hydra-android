package be.ugent.zeus.hydra.activities.common;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.plugins.LoaderPlugin;
import be.ugent.zeus.hydra.activities.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.activities.plugins.common.Plugin;
import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.LoaderCallback;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.requests.common.SimpleCacheRequest;

import java.io.Serializable;
import java.util.List;

/**
 * Activity that loads {@link CacheableRequest} using a loader.
 *
 * @author Niko Strijbol
 */
public abstract class LoaderToolbarActivity<D extends Serializable> extends HydraActivity implements LoaderCallback<D> {

    private static final String TAG = "LoaderToolbarActivity";

    protected boolean shouldRenew = false;
    //The progress bar. Is used if not null.
    protected ProgressBarPlugin barPlugin = new ProgressBarPlugin();
    protected LoaderPlugin<D> loaderPlugin = new LoaderPlugin<>(this, barPlugin);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(barPlugin);
        plugins.add(loaderPlugin);
    }

    @Override
    public Loader<ThrowableEither<D>> getLoader() {
        return new RequestAsyncTaskLoader<>(new SimpleCacheRequest<>(this, getRequest(), shouldRenew), this);
    }

    protected abstract CacheableRequest<D> getRequest();

    /**
     * When the request has failed.
     */
    public void receiveError(@NonNull Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh();
                    }
                })
                .show();
        barPlugin.hideProgressBar();
    }

    /**
     * Launch a new request.
     */
    protected void refresh() {
        Toast.makeText(getApplicationContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        shouldRenew = true;
        loaderPlugin.restartLoader();
        shouldRenew = false;
    }
}