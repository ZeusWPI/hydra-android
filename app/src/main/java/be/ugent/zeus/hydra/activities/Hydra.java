package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.activities.preferences.SettingsActivity;
import be.ugent.zeus.hydra.viewpager.SectionPagerAdapter;

/**
 * Main activity.
 */
public class Hydra extends ToolbarActivity {

    public static final String ARG_TAB = "argTab";
    private static final String TAG = "HydraActivity";
    private static final String PREF_ONBOARDING = "pref_onboarding";
    private static final int ONBOARDING_REQUEST = 5;

    //The tab icons
    private static int[] icons = {
            R.drawable.ic_tabs_home,
            R.drawable.ic_tabs_schamper,
            R.drawable.ic_tabs_menu,
            R.drawable.ic_tabs_events,
            R.drawable.ic_tabs_news,
            R.drawable.ic_tabs_info,
            R.drawable.ic_tabs_minerva,
            R.drawable.ic_tabs_urgent
    };

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //This activity has no parent.
        hasParent(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getToolBar().setDisplayShowTitleEnabled(false);

        //The first thing we do is maybe start the onboarding.
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean(PREF_ONBOARDING, true)) {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivityForResult(intent, ONBOARDING_REQUEST);
        } else { //Otherwise do init
            initialise();
        }
    }

    /**
     * Initialise the activity. Must be NOT be called BEFORE the onCreate function (you can call it in onCreate).
     */
    private void initialise() {
        ViewPager viewpager = $(R.id.pager);
        viewpager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                HydraApplication app = (HydraApplication) Hydra.this.getApplication();
                app.sendScreenName("Fragment tab: " + SectionPagerAdapter.names[position]);
            }
        });

        final AppBarLayout appBarLayout = $(R.id.app_bar_layout);
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
            }
        });

        TabLayout tabLayout = $(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewpager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }

        //Get start position
        int start = getIntent().getIntExtra(ARG_TAB, 0);
        viewpager.setCurrentItem(start, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimpliiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ONBOARDING_REQUEST) {
            if(resultCode == RESULT_OK) {
                Log.i(TAG, "Onboarding complete");
                preferences.edit().putBoolean(PREF_ONBOARDING, false).apply();
                initialise();
            } else {
                Log.i(TAG, "Onboarding failed, stop app.");
                finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}