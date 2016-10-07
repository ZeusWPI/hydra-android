package be.ugent.zeus.hydra.activities.common;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.LoaderCallbackHandler;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.requests.common.SimpleCacheRequest;

import java.io.Serializable;

/**
 * Activity that loads {@link CacheableRequest} using a loader.
 *
 * @author Niko Strijbol
 */
public abstract class LoaderToolbarActivity<D extends Serializable> extends ToolbarActivity implements LoaderCallbackHandler.LoaderCallback<D>, LoaderCallbackHandler.ProgressbarListener {

    private static final String TAG = "LoaderToolbarActivity";

    protected LoaderCallbackHandler<D> loaderHandler = new LoaderCallbackHandler<>(this, this);
    // ID of the loader.
    protected boolean shouldRenew = false;
    //The progress bar. Is used if not null.
    protected ProgressBar progressBar;

    /**
     * {@inheritDoc}
     *
     * Also sets the progress bar.
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        progressBar = $(R.id.progress_bar);
    }

    @Override
    public LoaderManager getTheLoaderManager() {
        return getSupportLoaderManager();
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
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
        hideProgressBar();
    }

    /**
     * Launch a new request.
     */
    protected void refresh() {
        Toast.makeText(getApplicationContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        shouldRenew = true;
        loaderHandler.restartLoader();
        shouldRenew = false;
    }
}