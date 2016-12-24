package be.ugent.zeus.hydra.activities.resto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.fragments.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.plugins.RequestPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.requests.resto.RestoMenuRequest;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.viewpager.MenuPagerAdapter;
import org.threeten.bp.LocalDate;

import java.util.List;

/**
 * Display the menu of the resto in a separate view, similar to the old app.
 *
 * @author Niko Strijbol
 */
public class MenuActivity extends HydraActivity implements AdapterView.OnItemSelectedListener {

    public static final String ARG_DATE = "start_date";

    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto";
    private RequestPlugin<RestoOverview> plugin;
    private MenuPagerAdapter pageAdapter;
    private ViewPager viewPager;
    private LocalDate startDate;

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin = RequestPlugin.cached(new RestoMenuRequest(this));
        plugin.hasProgress()
                .defaultError()
                .noToast()
                .setDataCallback(this::receiveData);
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);

        getToolbar().setDisplayShowTitleEnabled(false);

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

        Spinner spinner = $(R.id.resto_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getToolbar().getThemedContext(),
                R.array.resto_location,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        spinner.setSelection(Integer.parseInt(preferences.getString(RestoPreferenceFragment.PREF_RESTO, RestoPreferenceFragment.PREF_DEFAULT_RESTO)), true);
        spinner.setOnItemSelectedListener(this);

        Intent intent = getIntent();

        //Get the default start date
        if (intent.hasExtra(ARG_DATE)) {
            startDate = (LocalDate) intent.getSerializableExtra(ARG_DATE);
        } else {
            startDate = LocalDate.now();
        }

        plugin.startLoader();
    }

    private void receiveData(@NonNull RestoOverview data) {
        pageAdapter.setData(data);
        for (int i = 0; i < data.size(); i++) {
            RestoMenu menu = data.get(i);
            //Set the tab to this day!
            if (menu.getDate().isEqual(startDate)) {
                viewPager.setCurrentItem(i, true);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resto, menu);
        tintToolbarIcons(menu, R.id.action_refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                plugin.refresh();
                Toast.makeText(getApplicationContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.resto_show_website:
                NetworkUtils.maybeLaunchBrowser(this, URL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO, String.valueOf(position))
                .apply();
        plugin.refresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }
}