package be.ugent.zeus.hydra.common.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

/**
 * The base activity. Contains code related to common things for almost all activities.
 * Such features include:
 * <ul>
 *     <li>Support for the toolbar</li>
 *     <li>Better Google Analytics support</li>
 * </ul>
 *
 * @author Niko Strijbol
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Returns the action bar of this activity. If the ActionBar is not present or the method is called at the wrong
     * time, an {@link IllegalStateException} is thrown.
     *
     * @return The ActionBar.
     * @throws IllegalStateException If the method is called at the wrong time or there is no ActionBar.
     */
    @NonNull
    public ActionBar requireToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            throw new IllegalStateException("There is no ActionBar or the method is called at the wrong time.");
        } else {
            return actionBar;
        }
    }

    /**
     * Set the toolbar as action bar, and set it up to have an up button.
     */
    private void setUpActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //Set the up button.
        if (hasParent()) {
            requireToolbar().setDisplayHomeAsUpEnabled(true);
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
        tintToolbarIcons(requireToolbar(), menu, ids);
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
}