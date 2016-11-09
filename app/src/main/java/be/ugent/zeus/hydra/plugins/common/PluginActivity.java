package be.ugent.zeus.hydra.plugins.common;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class PluginActivity extends AppCompatActivity {

    private List<Plugin> plugins = new ArrayList<>();

    private boolean created;

    protected void addPlugin(Plugin plugin) {
        if(created) {
            throw new IllegalStateException("You must add plugins in the constructor or the function onAddPlugins().");
        }
        plugins.add(plugin);
    }

    /**
     * This method allows activities to register plugins. The plugins should be added to the list.
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
        created = true;
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;

        for(Plugin plugin: plugins) {
            plugin.onCreate(savedInstanceState);
        }
    }

    private Bundle savedInstance;

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