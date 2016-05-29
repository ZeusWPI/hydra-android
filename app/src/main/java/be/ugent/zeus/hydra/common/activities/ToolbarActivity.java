package be.ugent.zeus.hydra.common.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;

/**
 * An activity where there is no action bar, and instead a {@link android.support.v7.widget.Toolbar} is used.
 *
 * This class is designed to work with the XML file "toolbar.xml". Should you want another toolbar to be used,
 * please update the ID of the toolbar.
 *
 * This activity uses the NoActionBar theme.
 *
 * @author Niko Strijbol
 */
public abstract class ToolbarActivity extends AppCompatActivity {

    /**
     * The ID of the toolbar in the XML file. Change this if you want to use a different one.
     */
    protected int toolbarId = R.id.toolbar;

    /**
     * If the activity has a parent activity. If true, an up button will be shown.
     */
    protected boolean hasParent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Hydra_Main_NoActionBar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // If there is a parent, handle the up button.
        if(hasParent) {
            if (item.getItemId() == android.R.id.home) {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the toolbar as action bar, and set it up to have an up button, if
     */
    protected void setUpActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        //Set the up button.
        ActionBar actionBar = getSupportActionBar();
        if (hasParent && actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Replace an icon with given ID by the same icon but in white.
     *
     * @param menu The menu.
     * @param id The id if the icon.
     */
    protected void setWhiteIcon(Menu menu, int id) {
        Drawable drawable = DrawableCompat.wrap(menu.findItem(id).getIcon());
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.white));
        menu.findItem(id).setIcon(drawable);
    }
}
