package be.ugent.zeus.hydra.fragments.common;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.plugins.AutoStartLoaderPlugin;
import be.ugent.zeus.hydra.activities.plugins.LoaderPlugin;
import be.ugent.zeus.hydra.activities.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.activities.plugins.common.Plugin;
import be.ugent.zeus.hydra.activities.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.loaders.LoaderCallback;
import be.ugent.zeus.hydra.loaders.ProgressbarListener;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public abstract class LoaderFragment<D> extends PluginFragment implements LoaderCallback<D>, ProgressbarListener {

    private static final String TAG = "CachedLoaderFragment";

    protected boolean shouldRenew = false;
    protected ProgressBarPlugin barPlugin = new ProgressBarPlugin();
    protected LoaderPlugin<D> loaderPlugin = new AutoStartLoaderPlugin<D>(this, barPlugin, false);
    protected boolean autoStart = true;

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(barPlugin);
        plugins.add(loaderPlugin);
    }

    /**
     * Hide the progress bar.
     */
    public void hideProgressBar() {
        barPlugin.hideProgressBar();
    }

    /**
     * Show the progress bar.
     */
    public void showProgressBar() {
        barPlugin.showProgressBar();
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
        hideProgressBar();
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