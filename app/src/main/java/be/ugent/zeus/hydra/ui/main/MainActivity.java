package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.main.events.EventFragment;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment;
import be.ugent.zeus.hydra.ui.main.info.InfoFragment;
import be.ugent.zeus.hydra.ui.main.library.LibraryListFragment;
import be.ugent.zeus.hydra.ui.main.minerva.OverviewFragment;
import be.ugent.zeus.hydra.ui.main.news.NewsFragment;
import be.ugent.zeus.hydra.ui.main.resto.RestoFragment2;
import be.ugent.zeus.hydra.ui.main.schamper.SchamperFragment;
import be.ugent.zeus.hydra.ui.onboarding.OnboardingActivity;
import be.ugent.zeus.hydra.ui.preferences.SettingsActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import jonathanfinerty.once.Once;

/**
 * Main activity.
 *
 * The logic for handling the navigation drawer is not immediately obvious to those who do not work with it regularly.
 * Proceed with caution.
 */
public class MainActivity extends BaseActivity {

    public static final String ARG_TAB = "argTab";
    public static final String ARG_TAB_SHORTCUT = "argTabShortcut";
    public static final String ARG_NEW_DEFAULT = "argTabNewDefault";
    private static final String ARG_INITIAL_FRAGMENT = "argTabInitialFragment";

    private static final String TAG = "BaseActivity";

    private static final String ONCE_ONBOARDING = "once_onboarding";
    private static final int ONBOARDING_REQUEST = 5;

    private static final String ONCE_DRAWER = "once_drawer";

    private static final String FRAGMENT_MENU_ID = "backStack";

    private static final String SHORTCUT_RESTO = "resto";
    private static final String SHORTCUT_MINERVA = "minerva";

    private DrawerLayout drawer;
    private ProgressBar drawerLoader;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private AppBarLayout appBarLayout;
    private FirebaseAnalytics analytics;

    @IdRes
    private int initialFragmentId;

    /**
     * Contains the next fragment. This fragment will be shown when the navigation drawer has been closed, to prevent
     * lag. If null, nothing should happen on close.
     */
    @Nullable
    private Pair<Fragment, MenuItem> scheduledContentUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        analytics = FirebaseAnalytics.getInstance(this);

        // Show onboarding if the user has not completed it yet.
        if (!Once.beenDone(ONCE_ONBOARDING)) {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivityForResult(intent, ONBOARDING_REQUEST);
        }

        initialize(savedInstanceState);
    }

    private void initialize(@Nullable Bundle savedInstanceState) {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        appBarLayout = findViewById(R.id.app_bar_layout);
        drawerLoader = findViewById(R.id.drawer_loading);

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });

        toggle = new ActionBarDrawerToggle(this, drawer, findViewById(R.id.toolbar), R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Once.markDone(ONCE_DRAWER);
            }
        };
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // This is used to prevent lag during closing of the navigation drawer.
                if (scheduledContentUpdate != null) {
                    setFragment(scheduledContentUpdate.first, scheduledContentUpdate.second);
                    scheduledContentUpdate = null;
                }
            }
        });

        //If the instance is null, we must initialise a fragment, otherwise android does it for us.
        if (savedInstanceState == null) {
            // If we get a position, use that (for the shortcuts)
            if (getIntent().hasExtra(ARG_TAB_SHORTCUT)) {
                int position = getIntent().getIntExtra(ARG_TAB_SHORTCUT, 0);
                MenuItem menuItem = navigationView.getMenu().getItem(position);
                this.initialFragmentId = menuItem.getItemId();
                selectDrawerItem(menuItem);
            } else {
                // Get start position & select it
                int start = getIntent().getIntExtra(ARG_TAB, R.id.drawer_feed);
                this.initialFragmentId = start;
                selectDrawerItem(navigationView.getMenu().findItem(start));
            }
        } else { //Update title, since this is not saved apparently.
            //Current fragment
            FragmentManager manager = getSupportFragmentManager();
            Fragment current = manager.findFragmentById(R.id.content);
            setTitle(navigationView.getMenu().findItem(getFragmentMenuId(current)).getTitle());
            this.initialFragmentId = savedInstanceState.getInt(ARG_INITIAL_FRAGMENT, R.id.drawer_feed);
        }

        // If this is the first time, open the drawer.
        if (!Once.beenDone(ONCE_DRAWER)) {
            drawer.openDrawer(GravityCompat.START);
        }
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
     * Note: this function does not update the drawer if the fragment should be adjusted, only if another activity
     * should be opened.
     *
     * @param menuItem The item to display.
     */
    private void selectDrawerItem(MenuItem menuItem) {

        // First check if it are settings, then we don't update anything.
        if (menuItem.getItemId() == R.id.drawer_pref) {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return;
        }

        // Create a new fragment and specify the fragment to show based on nav item clicked
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
                fragment = new RestoFragment2();
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
                fragment = new OverviewFragment();
                break;
            case R.id.drawer_urgent:
                fragment = new UrgentFragment();
                break;
            case R.id.drawer_library:
                fragment = new LibraryListFragment();
                break;
            default:
                fragment = new ComingSoonFragment();
        }

        //Set the ID
        setArguments(fragment, menuItem.getItemId());

        //We use a back stack for the fragments. When a new fragment is shown, we insert it to the back stack.
        //If the fragment is already in the back stack, we restore the back stack to that fragment.
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Current fragment
        Fragment current = fragmentManager.findFragmentById(R.id.content);

        //If this is the same fragment, don't do anything.
        if (current != null && current.getClass().equals(fragment.getClass())) {
            if (current instanceof OnReselectListener) {
                ((OnReselectListener) current).onReselect(getIntent());
            }
            return;
        }

        // Show the toolbar, in case the new fragment is not scrollable. We must do this before the fragment
        // begins animating, otherwise glitches can occur.
        appBarLayout.setExpanded(true);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Hide the current fragment now, similar to how GMail handles things.
            if (current != null && current.getView() != null) {
                current.getView().setVisibility(View.GONE);
                drawerLoader.setVisibility(View.VISIBLE);
            }
            // Since there will be a delay, notify the fragment to prevent lingering snackbars or action modes.
            if (current instanceof ScheduledRemovalListener) {
                ((ScheduledRemovalListener) current).onRemovalScheduled();
            }
            Log.d(TAG, "selectDrawerItem: drawer is open, so scheduling update instead.");
            this.scheduledContentUpdate = new Pair<>(fragment, menuItem);
        } else {
            Log.d(TAG, "selectDrawerItem: drawer is closed, so doing update now.");
            setFragment(fragment, menuItem);
        }
        updateDrawer(menuItem);
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
        // Log it for Analytics
        HydraApplication application = (HydraApplication) getApplication();
        application.sendScreenName("Main > " + item.getTitle());
        analytics.setCurrentScreen(this, item.getTitle().toString(), null);
        // Close the navigation drawer
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setFragment(Fragment fragment, MenuItem menuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Get the ID of the current fragment.
        int id = menuItem.getItemId();

        // The name for the back stack and the fragment itself.
        String name = String.valueOf(menuItem.getItemId());

        // We remove all transactions.
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // When adding the new transaction, we only add it to the back stack if it is not the original one.
        FragmentTransaction  transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment, name);

        if (id != initialFragmentId) {
            Log.d(TAG, "setFragment: adding to back stack");
            transaction.addToBackStack(name);
        } else {
            Log.d(TAG, "setFragment: not adding to back stack");
        }

        transaction.commitAllowingStateLoss();
        drawerLoader.setVisibility(View.GONE);
    }

    /**
     * Implements the correct back-button behaviour for the drawer. If the drawer does not consume the back press,
     * the parent method is called.
     */
    @Override
    public void onBackPressed() {
        // If the drawer is open, close it.
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        // Add a listener.
        FragmentManager.OnBackStackChangedListener listener = () -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.content);
            int id = current == null ? initialFragmentId : getFragmentMenuId(current);
            MenuItem item = navigationView.getMenu().findItem(id);
            updateDrawer(item);
        };
        getSupportFragmentManager().addOnBackStackChangedListener(listener);

        super.onBackPressed();

        getSupportFragmentManager().removeOnBackStackChangedListener(listener);


//        // If the drawer is closed, we attempt to go back to the initial fragment if possible.
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
//        // If the fragment is not null, we might want to go back.
//        if (fragment != null) {
//            if (getFragmentMenuId(fragment) == initialFragmentId) {
//                // If the ID of the current fragment equals the original, call through to the super method.
//                super.onBackPressed();
//            } else {
//                // Otherwise, select the original fragment.
//                selectDrawerItem(navigationView.getMenu().findItem(initialFragmentId));
//            }
//        } else {
//            super.onBackPressed();
//        }
    }

    /**
     * Set the menu ID as argument for a fragment.
     *
     * @param fragment The fragment.
     * @param id The id.
     */
    private void setArguments(Fragment fragment, @IdRes int id) {
        Bundle arguments = new Bundle();
        arguments.putInt(FRAGMENT_MENU_ID, id);
        if (fragment instanceof ArgumentsReceiver) {
            ((ArgumentsReceiver) fragment).fillArguments(getIntent(), arguments);
        }
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
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Onboarding complete");
                Once.markDone(ONCE_ONBOARDING);
                analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null);
            } else {
                Log.w(TAG, "Onboarding failed, stop app.");
                finish();
            }
        }
        // We need to call this for the fragments to work properly.
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Change item while the activity is running.
        if (intent.hasExtra(ARG_TAB)) {
            int start = intent.getIntExtra(ARG_TAB, R.id.drawer_feed);
            if (intent.getBooleanExtra(ARG_NEW_DEFAULT, true)) {
                this.initialFragmentId = start;
            }
            selectDrawerItem(navigationView.getMenu().findItem(start));
        }
    }
}