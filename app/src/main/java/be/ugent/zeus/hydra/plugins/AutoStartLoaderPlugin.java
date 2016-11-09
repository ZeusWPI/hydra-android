package be.ugent.zeus.hydra.plugins;

import android.os.Bundle;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.loaders.LoaderCallback;

/**
 * @author Niko Strijbol
 */
public class AutoStartLoaderPlugin<D> extends LoaderPlugin<D> {

    private final boolean autoStart;

    public AutoStartLoaderPlugin(
            LoaderCallback<D> callback,
            LoaderCallback.DataCallbacks<D> dataCallbacks,
            @Nullable ProgressBarPlugin progressBarPlugin,
            boolean autoStart) {
        super(callback, dataCallbacks, progressBarPlugin);
        this.autoStart = true;
    }

    @Override
    protected void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(autoStart) {
            startLoader();
        }
    }
}
