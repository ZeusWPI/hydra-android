package be.ugent.zeus.hydra.activities.plugins;

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
            @Nullable ProgressBarPlugin progressBarPlugin,
            boolean autoStart) {
        super(callback, progressBarPlugin);
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
