package be.ugent.zeus.hydra.activities.common;

import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.plugins.common.PluginActivity;
import be.ugent.zeus.hydra.utils.ViewUtils;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Common activity. This activity supports:
 * - a toolbar
 * - analytics
 * - plugins
 *
 * @author Niko Strijbol
 */
public abstract class HydraActivity extends PluginActivity {

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

    protected ActionBar getToolbar() {
        assert getSupportActionBar() != null;
        return getSupportActionBar();
    }

    /**
     * Set the toolbar as action bar, and set it up to have an up button, if
     */
    private void setUpActionBar() {
        Toolbar toolbar = $(R.id.toolbar);

        setSupportActionBar(toolbar);

        //Set the up button.
        if (hasParent()) {
            getToolbar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setUpActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HydraApplication application = (HydraApplication) getApplicationContext();
        application.sendScreenName(getScreenName());
    }

    /**
     * Replace an icon with given ID by the same icon but in the correct colour.
     *
     * @param menu The menu.
     * @param ids The ids of the icon.
     */
    public void tintToolbarIcons(Menu menu, int... ids) {

        int color = ViewUtils.getColor(getToolbar().getThemedContext(), android.R.attr.textColorPrimary);

        for (int id: ids) {
            Drawable drawable = DrawableCompat.wrap(menu.findItem(id).getIcon());
            DrawableCompat.setTint(drawable, color);
            menu.findItem(id).setIcon(drawable);
        }
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