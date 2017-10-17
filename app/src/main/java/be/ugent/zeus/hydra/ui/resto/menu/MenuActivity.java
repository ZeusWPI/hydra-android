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
import be.ugent.zeus.hydra.data.network.requests.resto.SelectableMetaRequest;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.ui.resto.SelectableMetaViewModel;
import be.ugent.zeus.hydra.utils.Analytics;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import java8.util.Objects;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
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
    private MenuViewModel viewModel;
    private ArrayAdapter<RestoWrapper> restoAdapter;
    private Spinner spinner;

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

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);

        final AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
                HydraApplication app = (HydraApplication) MenuActivity.this.getApplication();
                app.sendScreenName("Menu tab: " + pageAdapter.getTabDate(position).toString());
                Bundle parameters = new Bundle();
                parameters.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, Analytics.Type.RESTO_MENU);
                parameters.putString(FirebaseAnalytics.Param.ITEM_NAME, pageAdapter.getPageTitle(position).toString());
                parameters.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(pageAdapter.getTabDate(position).toEpochDay()));
                analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, parameters);
            }
        });

        TabLayout tabLayout = findViewById(R.id.resto_tabs_slider);
        tabLayout.setupWithViewPager(viewPager);

        spinner = findViewById(R.id.resto_spinner);
        spinner.setEnabled(false);
        restoAdapter = new ArrayAdapter<>(getToolbar().getThemedContext(), android.R.layout.simple_spinner_item);
        restoAdapter.add(new RestoWrapper(getString(R.string.resto_spinner_loading)));
        restoAdapter.setDropDownViewResource(R.layout.x_simple_spinner_dropdown_item);
        spinner.setAdapter(restoAdapter);

        Intent intent = getIntent();

        //Get the default start date
        if (intent.hasExtra(ARG_DATE)) {
            startDate = (LocalDate) intent.getSerializableExtra(ARG_DATE);
        } else {
            startDate = LocalDate.now();
        }

        viewModel = ViewModelProviders.of(this).get(MenuViewModel.class);
        viewModel.getData().observe(this, ErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, SuccessObserver.with(this::receiveData));

        SelectableMetaViewModel metaViewModel = ViewModelProviders.of(this).get(SelectableMetaViewModel.class);
        metaViewModel.getData().observe(this, SuccessObserver.with(this::receiveResto));
    }

    private void receiveResto(@NonNull List<SelectableMetaRequest.RestoChoice> restos) {
        // Find index of the currently selected.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedKey = preferences.getString(RestoPreferenceFragment.PREF_RESTO_KEY, RestoPreferenceFragment.PREF_DEFAULT_RESTO);
        String defaultName = getString(R.string.resto_default_name);
        String selectedName = preferences.getString(RestoPreferenceFragment.PREF_RESTO_NAME, defaultName);
        SelectableMetaRequest.RestoChoice selectedChoice = new SelectableMetaRequest.RestoChoice(selectedName, selectedKey);
        int index = restos.indexOf(selectedChoice);
        if (index == -1) {
            // The key does not exist.
            SelectableMetaRequest.RestoChoice defaultChoice = new SelectableMetaRequest.RestoChoice(RestoPreferenceFragment.PREF_DEFAULT_RESTO, defaultName);
            index = restos.indexOf(defaultChoice);
        }
        // Set the things.
        List<RestoWrapper> wrappers = StreamSupport.stream(restos).map(RestoWrapper::new).collect(Collectors.toList());
        restoAdapter.clear();
        restoAdapter.addAll(wrappers);
        spinner.setSelection(index, false);
        spinner.setEnabled(true);
        findViewById(R.id.resto_spinner_progress).setVisibility(View.GONE);
        // Add the listener here to prevent multiple calls
        spinner.setOnItemSelectedListener(this);
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
                viewModel.onRefresh();
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

        // Get the item we selected.
        RestoWrapper wrapper = (RestoWrapper) parent.getItemAtPosition(position);
        SelectableMetaRequest.RestoChoice resto = wrapper.resto;

        if (resto == null || resto.getEndpoint() == null) {
            // Do nothing, as this should not happen.
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO_KEY, resto.getEndpoint())
                .putString(RestoPreferenceFragment.PREF_RESTO_NAME, resto.getName())
                .apply();
        //The start should be the day we have currently selected.
        if (pageAdapter.getCount() > viewPager.getCurrentItem()) {
            startDate = pageAdapter.getTabDate(viewPager.getCurrentItem());
        }
        viewModel.onRefresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> viewModel.onRefresh())
                .show();
    }

    private static class RestoWrapper {

        private final SelectableMetaRequest.RestoChoice resto;
        private final String string;

        public RestoWrapper(SelectableMetaRequest.RestoChoice resto) {
            this.resto = resto;
            this.string = null;
        }

        public RestoWrapper(String string) {
            this.resto = null;
            this.string = string;
        }

        @Override
        public String toString() {
            return resto == null ? string : resto.getName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RestoWrapper that = (RestoWrapper) o;
            return Objects.equals(resto, that.resto) &&
                    Objects.equals(string, that.string);
        }

        @Override
        public int hashCode() {
            return Objects.hash(resto, string);
        }
    }
}