package be.ugent.zeus.hydra.ui.common.plugins;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderResult;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.common.plugins.loader.LoaderPlugin;
import java8.util.function.Consumer;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Encapsulate the mechanics for a progress bar in a plugin.
 *
 * This plugin can be attached to a {@link LoaderPlugin} to hide the progress bar
 * automatically.
 *
 * @author Niko Strijbol
 */
public class ProgressBarPlugin extends Plugin {

    private ProgressBar progressBar;

    @Override
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = $(view, R.id.progress_bar);
    }

    public void register(LoaderPlugin<?> plugin) {
        plugin.setFinishedCallback(this::hideProgressBar);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public <D> Consumer<LoaderResult<D>> getFinishedCallback() {
        return r -> hideProgressBar();
    }
}