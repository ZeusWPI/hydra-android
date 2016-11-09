package be.ugent.zeus.hydra.fragments.common;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.plugins.AutoStartLoaderPlugin;
import be.ugent.zeus.hydra.plugins.LoaderPlugin;
import be.ugent.zeus.hydra.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.loaders.LoaderCallback;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public abstract class LoaderFragment<D> extends PluginFragment implements LoaderCallback<D>, LoaderCallback.DataCallbacks<D> {

    private static final String TAG = "CachedLoaderFragment";

    protected boolean shouldRenew = false;
    protected ProgressBarPlugin barPlugin = new ProgressBarPlugin();
    protected LoaderPlugin<D> loaderPlugin = new AutoStartLoaderPlugin<>(this,this, barPlugin, false);
    protected boolean autoStart = true;

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(barPlugin);
        plugins.add(loaderPlugin);
    }

    /**
     * Refresh the data.
     */
    protected void refresh() {
        Toast.makeText(getContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        this.shouldRenew = true;
        loaderPlugin.restartLoader();
        this.shouldRenew = false;
    }

    /**
     * This must be called when an error occurred.
     *
     * The default implementation displays a snackbar with the option to reload the data.
     *
     * @param error The exception.
     */
    @Override
    public void receiveError(@NonNull Throwable error) {
        assert getView() != null;
        barPlugin.hideProgressBar();
        Log.e(TAG, "Error while getting data.", error);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shouldRenew = true;
                        loaderPlugin.restartLoader();
                        shouldRenew = false;
                    }
                })
                .show();
    }
}