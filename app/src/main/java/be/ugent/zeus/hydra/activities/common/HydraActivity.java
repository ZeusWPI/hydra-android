package be.ugent.zeus.hydra.activities.common;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import be.ugent.zeus.hydra.activities.plugins.AnalyticsPlugin;
import be.ugent.zeus.hydra.activities.plugins.ToolbarPlugin;
import be.ugent.zeus.hydra.activities.plugins.common.Plugin;
import be.ugent.zeus.hydra.activities.plugins.common.PluginActivity;

import java.util.List;

/**
 * Abstract class with common methods and support for a toolbar.
 *
 * @author Niko Strijbol
 */
public abstract class HydraActivity extends PluginActivity {

    protected ToolbarPlugin toolbarPlugin = new ToolbarPlugin(hasParent());

    /**
     * Finds a view that was identified by the id attribute from the XML that was processed in {@link #onCreate}. This
     * version automatically casts the return value. It cannot be used for null values.
     *
     * @return The view if found or null otherwise.
     */
    @NonNull
    public <T extends View> T $(@IdRes int id) {
        @SuppressWarnings("unchecked")
        T v = (T) findViewById(id);
        assert v != null;
        return v;
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(new AnalyticsPlugin(this::getScreenName));
        plugins.add(toolbarPlugin);
    }

    protected String getScreenName() {
        return getClass().getSimpleName();
    }

    /**
     * Set if the activity has a parent or not.
     */
    protected boolean hasParent() {
        return true;
    }
}