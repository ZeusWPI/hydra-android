package be.ugent.zeus.hydra.fragments.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loaders.LoaderCallbackHandler;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public abstract class LoaderFragment<D> extends Fragment implements LoaderCallbackHandler.LoaderCallback<D>, LoaderCallbackHandler.ProgressbarListener {

    private static final String TAG = "CachedLoaderFragment";

    protected LoaderCallbackHandler<D> loaderHandler = new LoaderCallbackHandler<>(this, this);
    protected boolean shouldRenew = false;
    protected ProgressBar progressBar;
    protected boolean autoStart = true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = $(view, R.id.progress_bar);
    }

    /**
     * Hide the progress bar.
     */
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Show the progress bar.
     */
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Refresh the data.
     */
    protected void refresh() {
        Toast.makeText(getContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        this.shouldRenew = true;
        loaderHandler.restartLoader();
        this.shouldRenew = false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(autoStart) {
            loaderHandler.startLoader();
        }
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
                        loaderHandler.restartLoader();
                        shouldRenew = false;
                    }
                })
                .show();
    }

    @Override
    public LoaderManager getTheLoaderManager() {
        return getLoaderManager();
    }
}