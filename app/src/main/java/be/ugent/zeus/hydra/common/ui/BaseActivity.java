package be.ugent.zeus.hydra.common.ui;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewbinding.ViewBinding;

import java9.util.function.Function;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.ColourUtils;

/**
 * The base activity. Contains code related to common things for almost all activities.
 *
 * Such features include:
 * <ul>
 *     <li>Support for the toolbar</li>
 *     <li>Better Google Reporting support</li>
 *     <li>View binding, see below.</li>
 * </ul>
 *
 *
 * <h2>View binding</h2>
 *
 * This activity requires the use of view binding. To set up the view on the
 * activity, call {@link #setContentView(Function)}, to which you must pass
 * the view binding constructor.
 *
 * @author Niko Strijbol
 */
public abstract class BaseActivity<B extends ViewBinding> extends AppCompatActivity {

    protected B binding;

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
        Toolbar toolbar = ActivityCompat.requireViewById(this, R.id.toolbar);

        setSupportActionBar(toolbar);

        //Set the up button.
        if (hasParent()) {
            requireToolbar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setContentView(Function<LayoutInflater, B> binder) {
        this.binding = binder.apply(getLayoutInflater());
        setContentView(this.binding.getRoot());
        setUpActionBar();
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
        tintToolbarIcons(ColourUtils.resolveColour(toolbar.getThemedContext(), R.attr.colorControlNormal), menu, ids);
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

    /**
     * Set if the activity has a parent or not.
     */
    protected boolean hasParent() {
        return true;
    }
}
