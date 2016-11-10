package be.ugent.zeus.hydra.activities.resto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.loaders.DataCallback;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.plugins.RequestPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.requests.resto.RestoMenuRequest;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.viewpager.MenuPagerAdapter;
import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.List;

/**
 * Display the menu of the resto in a separate view, similar to the old app.
 *
 * @author Niko Strijbol
 */
public class MenuActivity extends HydraActivity implements DataCallback<RestoOverview> {

    public static final String ARG_DATE = "start_date";

    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto";
    private static final String TAG = "MenuActivity";

    private MenuPagerAdapter pageAdapter;
    private ViewPager viewPager;
    private LocalDate startDate;

    private final RequestPlugin<RestoOverview> plugin = new RequestPlugin<>(this, RequestPlugin.wrap(new RestoMenuRequest()));

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.getLoaderPlugin().setResetListener(() -> pageAdapter.setData(Collections.emptyList()));
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);
        
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pageAdapter = new MenuPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = $(R.id.resto_tabs_content);
        viewPager.setAdapter(pageAdapter);

        final AppBarLayout appBarLayout = $(R.id.app_bar_layout);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
                HydraApplication app = (HydraApplication) MenuActivity.this.getApplication();
                app.sendScreenName("Menu tab: " + pageAdapter.getTabDate(position));
            }
        });

        TabLayout tabLayout = $(R.id.resto_tabs_slider);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();

        //Get the default start date
        if(intent.hasExtra(ARG_DATE)) {
            startDate = (LocalDate) intent.getSerializableExtra(ARG_DATE);
        } else {
            startDate = LocalDate.now();
        }

        plugin.getLoaderPlugin().startLoader();
    }

    @Override
    public void receiveData(@NonNull RestoOverview data) {
        pageAdapter.setData(data);
        for (int i = 0; i < data.size(); i++) {
            RestoMenu menu = data.get(i);
            //Set the tab to this day!
            if(menu.getDate().isEqual(startDate)) {
                viewPager.setCurrentItem(i, true);
                break;
            }
        }
    }

    @Override
    public void receiveError(@NonNull Throwable e) {
        Log.e(TAG, "Error while getting data.", e);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> plugin.refresh())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resto, menu);
        toolbarPlugin.tintToolbarIcons(menu, R.id.resto_refresh);
        return super.onCreateOptionsMenu(menu);
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
}