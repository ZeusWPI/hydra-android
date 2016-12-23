package be.ugent.zeus.hydra.loaders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import be.ugent.zeus.hydra.plugins.common.FragmentPlugin;
import java8.util.function.Consumer;

/**
 * An {@link AbstractLoader} is fairly abstract. A consequence of this is that for a lot of use cases, boiler plate
 * code is needed. This plugin aims to prevent that by implementing the boiler plate for you, while stil being
 * flexible enough to be used in a lot of cases.
 *
 * One such improvement is the separation of callback methods, which prevents needing to implement empty methods.
 *
 * This plugin will automatically start the loader at the correct time, if used with a Fragment. When used with an
 * Activity, the loader will not be started automatically. If you don't want the loader to start even when using a
 * Fragment, use {@link #setAutoStart(boolean)}.
 *
 * The plugin supports multiple listeners:
 * - data callback
 * - error callback
 * - finish callback
 * - reset callback
 *
 * The first two are convenience methods for the third. The finish callback is called if there is an error or there
 * is data. The last three together are functionally equivalent to {@link android.support.v4.app.LoaderManager.LoaderCallbacks}.
 *
 * @author Niko Strijbol
 */
public class LoaderPlugin<D> extends FragmentPlugin implements LoaderManager.LoaderCallbacks<LoaderResult<D>> {

    private static final String TAG = "LoaderPlugin";

    private static int LOADER_ID = 0;

    private LoaderProvider<D> provider;
    private Consumer<D> dataConsumer;
    private Consumer<Throwable> errorConsumer;
    private Consumer<Loader<LoaderResult<D>>> resetListener;
    private Consumer<LoaderResult<D>> resultListener;

    private boolean autoStart = true;

    public LoaderPlugin(LoaderProvider<D> provider) {
        this.provider = provider;
    }

    protected LoaderPlugin() {
        //No-args constructor.
        //Only for use in subclasses.
    }

    protected LoaderPlugin<D> setLoaderProvider(LoaderProvider<D> loaderProvider) {
        this.provider = loaderProvider;
        return this;
    }

    /**
     * @param dataConsumer The data callback.
     */
    public LoaderPlugin<D> setDataCallback(Consumer<D> dataConsumer) {
        this.dataConsumer = dataConsumer;
        return this;
    }

    /**
     * @param errorConsumer The error callback.
     */
    public LoaderPlugin<D> setErrorCallback(Consumer<Throwable> errorConsumer) {
        this.errorConsumer = errorConsumer;
        return this;
    }

    public LoaderPlugin<D> setResetListener(Consumer<Loader<LoaderResult<D>>> resetListener) {
        this.resetListener = resetListener;
        return this;
    }

    public LoaderPlugin<D> setFinishCallback(Consumer<LoaderResult<D>> finishCallback) {
        this.resultListener = finishCallback;
        return this;
    }

    public LoaderPlugin<D> setAutoStart(boolean start) {
        this.autoStart = start;
        return this;
    }

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (autoStart) {
            startLoader();
        }
    }

    @Override
    public Loader<LoaderResult<D>> onCreateLoader(int id, Bundle args) {
        return provider.getLoader(getHost().getContext());
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<D>> loader, LoaderResult<D> data) {

        //If there is an error.
        if (data.hasError()) {
            if (errorConsumer != null) {
                errorConsumer.accept(data.getError().getCause());
            } else {
                Log.d(TAG, "Error occurred in loader, but no one was listening!", data.getError());
            }
        } else {
            if (dataConsumer != null) {
                dataConsumer.accept(data.getData());
            } else {
                Log.d(TAG, "Data was received, but not one was listening!");
            }
        }

        if (resultListener != null) {
            resultListener.accept(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<D>> loader) {
        if (resetListener != null) {
            resetListener.accept(loader);
        }
    }

    /**
     * Initialize the loader. Calls {@link LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)}.
     */
    public void startLoader() {
        getHost().getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * Restarts the loader. Calls {@link LoaderManager#restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)}.
     */
    public void restartLoader() {
        getHost().getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    /**
     * Destroys the loader. Calls {@link LoaderManager#destroyLoader(int)}.
     */
    public void destroyLoader() {
        getHost().getLoaderManager().destroyLoader(LOADER_ID);
    }
}