package be.ugent.zeus.hydra.activities.resto.common;

import android.support.annotation.MenuRes;
import android.view.Menu;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.LoaderToolbarActivity;

import java.io.Serializable;

/**
 * Abstract class for activities for the various resto feature. This class contains common logic for the action bar and
 * the overflow menu.
 *
 * @param <D> The type of data.
 *
 * @author Niko Strijbol
 */
public abstract class RestoActivity<D extends Serializable> extends LoaderToolbarActivity<D> {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(getMenuId(), menu);

        // We need to manually set the color of this Drawable for some reason.
        setWhiteIcons(menu, R.id.resto_refresh);

        return true;
    }

    /**
     * @return The ID of the menu to use.
     */
    @MenuRes
    protected abstract int getMenuId();

    /**
     * Add the refresh item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.resto_refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }
}