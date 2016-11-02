package be.ugent.zeus.hydra.activities.resto;

import android.view.Menu;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.LoaderToolbarActivity;
import be.ugent.zeus.hydra.utils.NetworkUtils;

import java.io.Serializable;

/**
 * Abstract class for activities for the various resto feature. This class contains common logic for the action bar and
 * the overflow menu, as well as the refresh stuff.
 *
 * @param <D> The type of data.
 *
 * @author Niko Strijbol
 */
public abstract class RestoActivity<D extends Serializable> extends LoaderToolbarActivity<D> {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resto, menu);
        tintToolbarIcons(menu, R.id.resto_refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.resto_show_website:
                NetworkUtils.maybeLaunchBrowser(this, getUrl());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @return The URL for the overflow button to display a website link.
     */
    protected abstract String getUrl();
}