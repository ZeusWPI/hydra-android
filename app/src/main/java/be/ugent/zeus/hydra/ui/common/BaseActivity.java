package be.ugent.zeus.hydra.ui.common;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

/**
 * The base activity. Contains code related to common things for almost all activities.
 * Such features include:
 * <ul>
 *     <li>Support for the toolbar</li>
 *     <li>Better Google Analytics support</li>
 *     <li>Finding views in a type-safe manner.</li>
 * </ul>
 *
 * @author Niko Strijbol
 */
public abstract class BaseActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    /**
     * Finds a view that was identified by the id attribute from the XML that was processed in {@link #onCreate}. This
     * version automatically casts the return value. It cannot be used for null values.
     *
     * @return The view if found or null otherwise.
     */
    @NonNull
    public <T extends View> T $(@IdRes int id) {
        T v = findViewById(id);
        assert v != null;
        return v;
    }

    /**
     * Get the toolbar. Don't call it when there is no toolbar, as it may crash.
     *
     * @return The toolbar.
     */
    @NonNull
    public ActionBar getToolbar() {
        assert getSupportActionBar() != null;
        return getSupportActionBar();
    }

    /**
     * Set the toolbar as action bar, and set it up to have an up button.
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
    protected void onResume() {
        super.onResume();
        HydraApplication application = (HydraApplication) getApplicationContext();
        application.sendScreenName(getScreenName());
    }

    /**
     * Replace an icon with given ID by the same icon but in the correct colour.
     *
     * @param menu The menu.
     * @param ids  The ids of the icon.
     */
    public void tintToolbarIcons(Menu menu, int... ids) {
        tintToolbarIcons(getToolbar(), menu, ids);
    }

    /**
     * Replace an icon with given ID by the same icon but in the correct colour.
     *
     * @param toolbar The toolbar to extract the colour from.
     * @param menu The menu.
     * @param ids  The ids of the icon.
     */
    public static void tintToolbarIcons(ActionBar toolbar, Menu menu, int... ids) {
        tintToolbarIcons(ViewUtils.getColor(toolbar.getThemedContext(), R.attr.colorControlNormal), menu, ids);
    }

    /**
     * Replace an icon with given ID by the same icon but in the given colour.
     *
     * @param colour The colour int.
     * @param menu The menu.
     * @param ids  The ids of the icon.
     */
    public static void tintToolbarIcons(@ColorInt int colour, Menu menu, int... ids) {
        for (int id : ids) {
            Drawable drawable = DrawableCompat.wrap(menu.findItem(id).getIcon());
            DrawableCompat.setTint(drawable, colour);
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

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}