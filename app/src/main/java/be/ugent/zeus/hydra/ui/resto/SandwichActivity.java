package be.ugent.zeus.hydra.ui.resto;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.Sandwich;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.resto.SandwichRequest;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.List;

/**
 * Activity that shows a list of sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichActivity extends BaseActivity {

    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes/overzicht.htm";

    private SandwichAdapter adapter = new SandwichAdapter();
    private RecyclerViewPlugin<Sandwich> plugin = new RecyclerViewPlugin<>(Requests.cachedArray(new SandwichRequest()), adapter);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.defaultError().enableProgress();
        plugins.add(plugin);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_sandwich);

        RecyclerView recyclerView = plugin.getRecyclerView();
        plugin.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = $(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);

        plugin.startLoader();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                plugin.refresh();
                return true;
            case R.id.resto_show_website:
                NetworkUtils.maybeLaunchBrowser(this, URL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resto, menu);
        tintToolbarIcons(menu, R.id.action_refresh);
        return true;
    }
}