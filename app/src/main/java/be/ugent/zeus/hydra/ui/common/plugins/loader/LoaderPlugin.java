package be.ugent.zeus.hydra.ui.common.plugins.loader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import be.ugent.zeus.hydra.ui.common.plugins.common.FragmentPlugin;
import be.ugent.zeus.hydra.ui.common.loaders.AbstractLoader;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderResult;
import be.ugent.zeus.hydra.utils.FunctionalUtils;
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

    private LoaderCallback<D> loaderCallback;
    private Consumer<D> successCallback;
    private Consumer<Throwable> errorCallback;

    private Runnable finishedCallback;
    private Runnable resetCallback;

    private boolean autoStart = true;

    public LoaderPlugin(LoaderCallback<D> provider) {
        this.loaderCallback = provider;
    }

    protected LoaderPlugin() {
        //No-args constructor.
        //Only for use in subclasses.
    }

    protected void setLoaderProvider(LoaderCallback<D> loaderCallback) {
        this.loaderCallback = loaderCallback;
    }

    /**
     * Set the success callback receiver.
     *
     * @param successCallback The callback.
     */
    public void setSuccessCallback(Consumer<D> successCallback) {
        this.successCallback = successCallback;
    }

    /**
     * Set an error listener. Existing listeners will still be called. To remove all callbacks, pass null.
     *
     * @param errorCallback An additional listener, or null to remove all listeners.
     */
    public void addErrorCallback(@Nullable Consumer<Throwable> errorCallback) {
        this.errorCallback = FunctionalUtils.maybeAndThen(this.errorCallback, errorCallback);
    }

    /**
     * Set a reset listener. Existing listeners will still be called. To remove all callbacks, pass null.
     *
     * @param resetListener An additional listener, or null to remove all listeners.
     */
    public void addResetCallback(@Nullable Runnable resetListener) {
        if (this.resetCallback == null || resetListener == null) {
            this.resetCallback = resetListener;
        } else {
            this.resetCallback = () -> {
                resetCallback.run();
                resetListener.run();
            };
        }
    }

    public void setFinishedCallback(Runnable runnable) {
        this.finishedCallback = runnable;
    }

    /**
     * Set a result listener. Existing listeners will still be called. To remove all callbacks, pass null.
     *
     * @param resultListener An additional listener, or null to remove all listeners.
     */
    public void addSuccessCallback(@Nullable Consumer<D> resultListener) {
        this.successCallback = FunctionalUtils.maybeAndThen(this.successCallback, resultListener);
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
        return loaderCallback.getLoader(args);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<D>> loader, LoaderResult<D> data) {
        // If there is an error.
        if (data.hasError()) {
            if (errorCallback != null) {
                errorCallback.accept(data.getError().getCause());
            } else {
                Log.d(TAG, "Error occurred in loader, but no one was listening!", data.getError());
            }
        } else {
            if (successCallback != null) {
                successCallback.accept(data.getData());
            } else {
                Log.d(TAG, "Data was received, but not one was listening!");
            }
        }
        if (finishedCallback != null) {
            finishedCallback.run();
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<D>> loader) {
        if (resetCallback != null) {
            resetCallback.run();
        }
    }

    /**
     * Initialize the loader. Calls {@link LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)}.
     */
    public void startLoader() {
        getHost().getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * Initialize the loader. Calls {@link LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)}.
     *
     * @param args The arguments to construct the loader.
     */
    public void startLoader(Bundle args) {
        getHost().getLoaderManager().initLoader(LOADER_ID, args, this);
    }

    /**
     * Restarts the loader. Calls {@link LoaderManager#restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)}.
     */
    public void restartLoader() {
        getHost().getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    /**
     * Restarts the loader. Calls {@link LoaderManager#restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)}.
     *
     * @param args The arguments to reconstruct the loader.
     */
    public void restartLoader(Bundle args) {
        getHost().getLoaderManager().restartLoader(LOADER_ID, args, this);
    }

    /**
     * Destroys the loader. Calls {@link LoaderManager#destroyLoader(int)}.
     */
    public void destroyLoader() {
        getHost().getLoaderManager().destroyLoader(LOADER_ID);
    }
}