package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.activities.preferences.SettingsActivity;
import be.ugent.zeus.hydra.events.EventFragment;
import be.ugent.zeus.hydra.fragments.*;
import be.ugent.zeus.hydra.fragments.minerva.MinervaFragment;
import be.ugent.zeus.hydra.fragments.resto.RestoFragment;
import be.ugent.zeus.hydra.fragments.urgent.UrgentFragment;
import be.ugent.zeus.hydra.homefeed.HomeFeedFragment;
import be.ugent.zeus.hydra.library.LibraryListFragment;
import be.ugent.zeus.hydra.plugins.OfflinePlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;

import java.util.List;

/**
 * Main activity.
 */
public class MainActivity extends HydraActivity {

    public static final String ARG_TAB = "argTab";
    private static final String TAG = "HydraActivity";
    private static final String PREF_ONBOARDING = "pref_onboarding";
    private static final int ONBOARDING_REQUEST = 5;

    private static final String FRAGMENT_MENU_ID = "backStack";

    private static final String SHORTCUT_RESTO = "resto";
    private static final String SHORTCUT_MINERVA = "minerva";

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private AppBarLayout appBarLayout;

    private SharedPreferences preferences;
    private OfflinePlugin plugin = new OfflinePlugin();

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The first thing we do is maybe start the onboarding.
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(PREF_ONBOARDING, true)) {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivityForResult(intent, ONBOARDING_REQUEST);
        }

        initialize(savedInstanceState);
    }

    private void initialize(Bundle savedInstanceState) {
        drawer = $(R.id.drawer_layout);
        navigationView = $(R.id.navigation_view);
        appBarLayout = $(R.id.app_bar_layout);

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });

        toggle = new ActionBarDrawerToggle(this, drawer, $(R.id.toolbar), R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }
        };
        drawer.addDrawerListener(toggle);
        plugin.setView($(android.R.id.content));

        //If the instance is null, we must initialise a fragment, otherwise android does it for us.
        if (savedInstanceState == null) {
            //Get start position & select it
            int start = getIntent().getIntExtra(ARG_TAB, 0);
            selectDrawerItem(navigationView.getMenu().getItem(start));
        } else { //Update title, since this is not saved apparently.
            //Current fragment
            FragmentManager manager = getSupportFragmentManager();
            Fragment current = manager.findFragmentById(R.id.content);
            setTitle(navigationView.getMenu().findItem(getFragmentMenuId(current)).getTitle());
        }
    }

    public OfflinePlugin getOfflinePlugin() {
        return plugin;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Select a drawer item. This will update the fragment to the new one from the menu item, if it is a different one
     * than the current one.
     *
     * Note: this function does not update the drawer.
     *
     * @param menuItem The item to display.
     */
    private void selectDrawerItem(MenuItem menuItem) {

        updateDrawer(menuItem);

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Log.d(TAG, "selectDrawerItem: Selecting item");

        Fragment fragment;
        switch (menuItem.getItemId()) {
            case R.id.drawer_feed:
                fragment = new HomeFeedFragment();
                break;
            case R.id.drawer_schamper:
                fragment = new SchamperFragment();
                break;
            case R.id.drawer_resto:
                reportShortcutUsed(SHORTCUT_RESTO);
                fragment = new RestoFragment();
                break;
            case R.id.drawer_events:
                fragment = new EventFragment();
                break;
            case R.id.drawer_news:
                fragment = new NewsFragment();
                break;
            case R.id.drawer_info:
                fragment = new InfoFragment();
                break;
            case R.id.drawer_minerva:
                reportShortcutUsed(SHORTCUT_MINERVA);
                fragment = new MinervaFragment();
                break;
            case R.id.drawer_urgent:
                fragment = new UrgentFragment();
                break;
            case R.id.drawer_library:
                fragment = new LibraryListFragment();
                break;
            case R.id.drawer_pref: //Launch new activity
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return;
            default:
                fragment = new ComingSoonFragment();
        }

        //Set the ID
        setArguments(fragment, menuItem.getItemId());

        //We use a back stack for the fragments. When a new fragment is shown, we add it to the back stack.
        //If the fragment is already in the back stack, we restore the back stack to that fragment.
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Current fragment
        Fragment current = fragmentManager.findFragmentById(R.id.content);

        //If this is the same fragment, don't do anything.
        if(current != null && current.getClass().equals(fragment.getClass())) {
            return;
        }

        String name = String.valueOf(menuItem.getItemId());
        fragmentManager.popBackStackImmediate(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

        appBarLayout.setExpanded(true);
    }

    /**
     * Manually set the drawer item, and close the drawers. This will not update the fragment.
     *
     * @param item The item to mark as current.
     */
    private void updateDrawer(MenuItem item) {
        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Close the navigation drawer
        drawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        //If it is empty, we attempt to go the the start one.
        //Update the title
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment != null) {
            int id = getFragmentMenuId(fragment);
            MenuItem item = navigationView.getMenu().findItem(id);
            MenuItem original = navigationView.getMenu().getItem(getIntent().getIntExtra(ARG_TAB, 0));

            //We are good
            if (item == original) {
                super.onBackPressed();
            } else {
                selectDrawerItem(original);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void setArguments(Fragment fragment, @IdRes int id) {
        Bundle arguments = new Bundle();
        arguments.putInt(FRAGMENT_MENU_ID, id);
        fragment.setArguments(arguments);
    }

    /**
     * Get the position of a fragment in the menu. While stupid, there is no other way to do this.
     */
    @IdRes
    private int getFragmentMenuId(Fragment fragment) {
        return fragment.getArguments().getInt(FRAGMENT_MENU_ID);
    }

    @Override
    protected boolean hasParent() {
        return false;
    }

    private void reportShortcutUsed(String shortcutId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Log.d(TAG, "Report shortcut use: " + shortcutId);
            ShortcutManager manager = getSystemService(ShortcutManager.class);
            try {
                manager.reportShortcutUsed(shortcutId);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error while reporting shortcut usage:", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ONBOARDING_REQUEST) {
            if(resultCode == RESULT_OK) {
                Log.i(TAG, "Onboarding complete");
                preferences.edit().putBoolean(PREF_ONBOARDING, false).apply();
                initialize(null);
            } else {
                Log.i(TAG, "Onboarding failed, stop app.");
                finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}