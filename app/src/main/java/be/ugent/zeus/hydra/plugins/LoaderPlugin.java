package be.ugent.zeus.hydra.plugins;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import be.ugent.zeus.hydra.loaders.DataCallback;
import be.ugent.zeus.hydra.loaders.LoaderProvider;
import be.ugent.zeus.hydra.loaders.ResetListener;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.plugins.common.FragmentPlugin;

/**
 * @author Niko Strijbol
 */
public class LoaderPlugin<D> extends FragmentPlugin implements LoaderManager.LoaderCallbacks<ThrowableEither<D>> {

    private static int LOADER_ID = 0;

    private final LoaderProvider<D> provider;
    private final DataCallback<D> dataCallbacks;
    private ResetListener resetListener;

    private final ProgressBarPlugin progressBarPlugin;

    private boolean autoStart = true;

    public LoaderPlugin(
            LoaderProvider<D> provider,
            DataCallback<D> dataCallbacks,
            @Nullable ProgressBarPlugin progressBarPlugin) {
        this.provider = provider;
        this.progressBarPlugin = progressBarPlugin;
        this.dataCallbacks = dataCallbacks;
    }

    public void setAutoStart(boolean start) {
        this.autoStart = start;
    }

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(autoStart) {
            startLoader();
        }
    }

    public void setResetListener(ResetListener resetListener) {
        this.resetListener = resetListener;
    }

    @Override
    public Loader<ThrowableEither<D>> onCreateLoader(int id, Bundle args) {
        return provider.getLoader(getHost().getContext());
    }

    @Override
    public void onLoadFinished(Loader<ThrowableEither<D>> loader, ThrowableEither<D> data) {
        if(data.hasError()) {
            dataCallbacks.receiveError(data.getError());
        } else {
            dataCallbacks.receiveData(data.getData());
        }

        if(progressBarPlugin != null) {
            progressBarPlugin.hideProgressBar();
        }
    }

    @Override
    public void onLoaderReset(Loader<ThrowableEither<D>> loader) {
        if(resetListener != null) {
            resetListener.onLoaderReset();
        }
    }

    public void startLoader() {
        getHost().getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void restartLoader() {
        getHost().getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void destroyLoader() {
        getHost().getLoaderManager().destroyLoader(LOADER_ID);
    }
}
