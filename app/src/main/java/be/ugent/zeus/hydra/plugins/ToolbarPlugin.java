package be.ugent.zeus.hydra.plugins;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.plugins.common.ContextProvider;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.utils.ViewUtils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class ToolbarPlugin extends Plugin {

    private final boolean hasParent;

    public ToolbarPlugin() {
        this(true);
    }

    public ToolbarPlugin(boolean hasParent) {
        this.hasParent = hasParent;
    }

    private AppCompatActivity getActivity() {
        return ((ContextProvider.ActivityProvider) getHost()).getActivity();
    }

    @Override
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpActionBar(view);
    }

    public ActionBar getToolBar() {
        return getActivity().getSupportActionBar();
    }

    /**
     * Set the toolbar as action bar, and set it up to have an up button, if
     */
    private void setUpActionBar(View view) {
        Toolbar toolbar = $(view, R.id.toolbar);

        getActivity().setSupportActionBar(toolbar);

        //Set the up button.
        if (hasParent) {
            getToolBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Replace an icon with given ID by the same icon but in the correct colour.
     *
     * @param menu The menu.
     * @param ids The ids of the icon.
     */
    public void tintToolbarIcons(Menu menu, int... ids) {

        int color = ViewUtils.getColor(getToolBar().getThemedContext(), android.R.attr.textColorPrimary);

        for (int id: ids) {
            Drawable drawable = DrawableCompat.wrap(menu.findItem(id).getIcon());
            DrawableCompat.setTint(drawable, color);
            menu.findItem(id).setIcon(drawable);
        }
    }
}
