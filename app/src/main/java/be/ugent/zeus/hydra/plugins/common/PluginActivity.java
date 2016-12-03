package be.ugent.zeus.hydra.plugins.common;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that hosts plugins.
 *
 * To use plugins in your activity, extend this class. Then add the needed plugins in {@link #onAddPlugins(List)}.
 *
 * @author Niko Strijbol
 */
public abstract class PluginActivity extends AppCompatActivity {

    private List<Plugin> plugins = new ArrayList<>();
    private Bundle savedInstance;

    /**
     * This method allows to register plugins. The plugins should be added to the given list. This method is also
     * suitable for initialising the plugin, as it will be called before anything else.
     *
     * @param plugins The list.
     */
    @CallSuper
    protected void onAddPlugins(List<Plugin> plugins) {
        //Nothing
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onAddPlugins(plugins);
        List<Plugin> recursive = new ArrayList<>();
        for(Plugin plugin: plugins) {
            plugin.onAddPlugins(recursive);
        }
        plugins.addAll(recursive);
        for(Plugin plugin: plugins) {
            plugin.setProvider(ContextProvider.getProvider(this));
        }
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;

        for(Plugin plugin: plugins) {
            plugin.onCreate(savedInstanceState);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        for(Plugin plugin: plugins) {
            plugin.onViewCreated(findViewById(android.R.id.content), savedInstance);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for(Plugin plugin: plugins) {
            plugin.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(Plugin plugin: plugins) {
            plugin.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for(Plugin plugin: plugins) {
            plugin.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for(Plugin plugin: plugins) {
            plugin.onStop();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        for(Plugin plugin: plugins) {
            plugin.onRestart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(Plugin plugin: plugins) {
            plugin.onDestroy();
        }
    }
}