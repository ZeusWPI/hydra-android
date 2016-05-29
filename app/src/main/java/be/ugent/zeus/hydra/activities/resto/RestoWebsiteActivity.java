package be.ugent.zeus.hydra.activities.resto;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.MenuRes;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;

/**
 * Activity for classes that have a URL option in the overflow menu.
 * @author Niko Strijbol
 */
public abstract class RestoWebsiteActivity<D extends Parcelable> extends RestoActivity<D> {

    /**
     * Add the website option.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.resto_show_website) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @return The URL for the overflow button to display a website link.
     */
    protected abstract String getUrl();

    /**
     * @return The ID of the menu to use.
     */
    @MenuRes
    protected int getMenuId() {
        return R.menu.menu_resto;
    }
}