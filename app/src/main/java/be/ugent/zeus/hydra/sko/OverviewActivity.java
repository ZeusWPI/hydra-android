package be.ugent.zeus.hydra.sko;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;

/**
 * SKO overview activity. Only displays the line-up.
 *
 * @author Niko Strijbol
 */
public class OverviewActivity extends BaseActivity {

    private static final String SKO_WEBSITE = Endpoints.SKO;

    /**
     * Start the activity on the default tab.
     *
     * @param context The context.
     *
     * @return The intent to start the activity.
     */
    public static Intent start(Context context) {
        return new Intent(context, OverviewActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sko_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sko, menu);
        tintToolbarIcons(menu, R.id.sko_visit_website);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sko_visit_website) {
            NetworkUtils.maybeLaunchBrowser(this, SKO_WEBSITE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
