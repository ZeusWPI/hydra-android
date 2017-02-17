package be.ugent.zeus.hydra.plugins.common;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * A plugin is a light weight object that can receive a small subset of the Activity/Fragment lifecycle callbacks.
 *
 * You can add plugins to {@link PluginFragment} or {@link PluginActivity}, which will enable them.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Plugin {

    private ContextProvider.Provider provider;

    protected void setProvider(ContextProvider.Provider provider) {
        this.provider = provider;
    }

    @NonNull
    protected ContextProvider.Provider getHost() {
        if (provider == null) {
            throw new IllegalStateException("getHost was called too early.");
        }
        return provider;
    }

    /**
     * You should call the super method AFTER adding your own.
     *
     * @param plugins The list of plugins.
     */
    @CallSuper
    protected void onAddPlugins(List<Plugin> plugins) {
        List<Plugin> recursive = new ArrayList<>();
        for (Plugin plugin : plugins) {
            plugin.onAddPlugins(recursive);
        }
        plugins.addAll(recursive);
    }

    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {}

    /**
     * In an activity, this is called after {@link android.app.Activity#setContentView(int)}.
     */
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {}

    @CallSuper
    protected void onStart() {}

    @CallSuper
    protected void onResume() {}

    @CallSuper
    protected void onPause() {}

    @CallSuper
    protected void onStop() {}

    @CallSuper
    protected void onRestart() {}

    @CallSuper
    protected void onDestroy() {}
}