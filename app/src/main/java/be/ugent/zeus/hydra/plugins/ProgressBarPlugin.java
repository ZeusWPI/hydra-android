package be.ugent.zeus.hydra.plugins;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.plugins.common.Plugin;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class ProgressBarPlugin extends Plugin {

    private ProgressBar progressBar;

    @Override
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = $(view, R.id.progress_bar);
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
}