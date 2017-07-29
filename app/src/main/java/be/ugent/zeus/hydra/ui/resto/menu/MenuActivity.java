package be.ugent.zeus.hydra.ui.resto.menu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import org.threeten.bp.LocalDate;

import java.util.List;

/**
 * Display the menu of the resto in a separate view, similar to the old app.
 *
 * @author Niko Strijbol
 */
public class MenuActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MenuActivity";

    public static final String ARG_DATE = "start_date";

    private static final String URL = "https://www.ugent.be/student/nl/meer-dan-studeren/resto";
    private MenuPagerAdapter pageAdapter;
    private ViewPager viewPager;
    private LocalDate startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);

        getToolbar().setDisplayShowTitleEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pageAdapter = new MenuPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = findViewById(R.id.resto_tabs_content);
        viewPager.setAdapter(pageAdapter);

        final AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
                HydraApplication app = (HydraApplication) MenuActivity.this.getApplication();
                app.sendScreenName("Menu tab: " + pageAdapter.getTabDate(position).toString());
            }
        });

        TabLayout tabLayout = findViewById(R.id.resto_tabs_slider);
        tabLayout.setupWithViewPager(viewPager);

        Spinner spinner = findViewById(R.id.resto_spinner);
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

        MenuViewModel model = ViewModelProviders.of(this).get(MenuViewModel.class);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, new ProgressObserver<>(findViewById(R.id.progress_bar)));
        model.getData().observe(this, SuccessObserver.with(this::receiveData));
    }

    private void receiveData(@NonNull List<RestoMenu> data) {
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
                onRefresh();
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
        //The start should be the day we have currently selected.
        if (pageAdapter.getCount() > viewPager.getCurrentItem()) {
            startDate = pageAdapter.getTabDate(viewPager.getCurrentItem());
        }
        onRefresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> onRefresh())
                .show();
    }

    public void onRefresh() {
        RefreshBroadcast.broadcastRefresh(this, true);
    }
}