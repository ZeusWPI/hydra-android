package be.ugent.zeus.hydra.common.main;

import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.minerva.mainui.OverviewFragment;
import be.ugent.zeus.hydra.onboarding.OnboardingActivity;
import be.ugent.zeus.hydra.schamper.list.SchamperFragment;
import be.ugent.zeus.hydra.association.event.list.EventFragment;
import be.ugent.zeus.hydra.feed.HomeFeedFragment;
import be.ugent.zeus.hydra.info.list.InfoFragment;
import be.ugent.zeus.hydra.library.list.LibraryListFragment;
import be.ugent.zeus.hydra.association.news.list.NewsFragment;
import be.ugent.zeus.hydra.resto.menu.RestoFragment;
import be.ugent.zeus.hydra.common.preferences.SettingsActivity;
import be.ugent.zeus.hydra.urgent.UrgentFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import jonathanfinerty.once.Once;

/**
 * Main activity.
 *
 * The logic for handling the navigation drawer is not immediately obvious to those who do not work with it regularly.
 * Proceed with caution.
 *
 * <h1>Navigation</h1>
 *
 * One of the main responsibilities of this activity is managing navigation between the drawer, the fragments and the
 * fragments between each other. The navigation is built in two main components: navigation forward and navigating
 * backwards. Each component itself is not that difficult. Together the provide an intuitive navigation.
 *
 * <h2>Forward navigation</h2>
 *
 * When the user navigates to a new fragment in this activity, the back stack (of the {@link #getSupportFragmentManager()}
 * must be updated. There are three scenario's to consider:
 * <ol>
 *     <li>
 *         The navigation originates from the navigation drawer. Implementation note: these events will be received
 *         by the {@link #onNavigationItemSelected(MenuItem)} method. Represented by {@link NavigationSource#DRAWER}.
 *     </li>
 *     <li>
 *         The navigation originates from somewhere else, probably from inside a fragment. Implementation note: these
 *         events will be received in the {@link #onNewIntent(Intent)} method. Represented by {@link NavigationSource#INNER}.
 *     </li>
 *     <li>
 *         The last case is in fact the easiest: it is the base case, what happens when the activity is created for the
 *         first time. Represented by {@link NavigationSource#INITIALISATION}.
 *     </li>
 * </ol>
 *
 * On the first scenario, the back stack should be cleared [1]. The new fragment should not be added to the back stack,
 * as it should not be reversed. An implementation complexity is that this should not happen when restoring the
 * activity (scenario three).
 * TODO: investigate if it would be better to always return to the homefeed instead of clearing the back button?
 *
 * In the second scenario, the user probably expects to be able to go back. Therefore, these should be added to the
 * back stack. Currently it is unlikely this will cause back stacks that are too large. In the future, we might need
 * to restrict this, e.g. by checking for existing instances of a fragment in the stack and popping until that instance
 * is reached. For example, assume the back stack is B -> A -> C. When the user then navigates to fragment A, the C
 * fragment should be removed from the stack.
 *
 * The third and last scenario is the easiest: nothing should be done when the activity is first started or recreated.
 * The fragments should not be added to the back stack.
 *
 * <h2>Backwards navigation</h2>
 *
 * The logic above makes the backwards navigation quite simple, and can be summarized as:
 * <pre>{@code
 * +-----------------------+
 * |  Back button pressed  |
 * +------+----------------+
 *        |
 *        |
 *        |
 * +------v----------------+  Yes   +-----------------------+
 * |  Is drawer open?      +-------->  Close drawer         |
 * +------+----------------+        +-----------------------+
 *        |
 *        | No
 *        |
 * +------v----------------+  Yes   +-----------------------+
 * |  Is back stack empty? +-------->  Pop from back stack  |
 * +------+----------------+        +-----------------------+
 *        |
 *        | No
 *        |
 * +------v----------------+
 * |  Let activity finish  |
 * +-----------------------+
 * }</pre>
 *
 * <h1>Arguments for children</h1>
 *
 * If a child fragment needs arguments to initialise, it should implement {@link ArgumentsReceiver}. When implementing
 * this interface, the activity will call this method on the fragment. This gives the fragment a chance to extract
 * arguments from the intent of the activity, which will then be set as arguments to the fragment.
 *
 * After this method call, the fragment can behave as if the arguments were directly set on the fragment itself.
 *
 * This function will only be called when creating a fragment, not when popping from the back stack.
 *
 * <h1>Common views and removal</h1>
 *
 * The activity provides some common views:
 * <ul>
 *     <li>A {@link android.support.design.widget.TabLayout} with id {@link R.id#tab_layout}</li>
 *     <li>A {@link android.support.design.widget.BottomNavigationView} with id {@link R.id#bottom_navigation}</li>
 * </ul>
 *
 * These are hidden by default and should be shown and hidden by the fragments that use them.
 *
 * To this end, a fragment may implement {@link ScheduledRemovalListener}. This callback will called when the activity
 * determines that a fragment is no longer in use and fragment-specific views should be hidden.
 *
 * The fragment itself should not be hidden; the activity takes care of this. The callback is specifically for views
 * outside the normal fragment, such as the two above or a contextual action bar.
 *
 * The reason fragments cannot fully rely on the default lifecycle methods, such as {@link Fragment#onStop()}, is that
 * the fragment is not always removed immediately by the activity when it is hidden (this as to do with performance).
 *
 * <h1>Arguments</h1>
 *
 * The activity has one public argument: which child fragment to load. The preferred way of using it is
 * {@link #ARG_TAB}, with an int value. The int value is the ID of the menu item in the drawer.
 *
 * @see [1] <a href="https://youtu.be/Sww4omntVjs?t=13m46s">Android Design in Action: Navigation Anti-Patterns, pattern 6</a>
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_TAB = "argTab";
    public static final String ARG_TAB_SHORTCUT = "argTabShortcut";

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

    /**
     * Contains the next fragment. This fragment will be shown when the navigation drawer has been closed, to prevent
     * lag. If null, nothing should happen on close.
     */
    @Nullable
    private DrawerUpdate scheduledContentUpdate;

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

        // Register the listener for navigation events from the drawer.
        navigationView.setNavigationItemSelectedListener(this);

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
                    setFragment(scheduledContentUpdate.fragment, scheduledContentUpdate.menuItem, scheduledContentUpdate.navigationSource);
                    scheduledContentUpdate = null;
                }
            }
        });

        // If the instance is null, we must initialise a fragment, otherwise android does it for us.
        if (savedInstanceState == null) {
            // If we get a position, use that (for the shortcuts)
            if (getIntent().hasExtra(ARG_TAB_SHORTCUT)) {
                int position = getIntent().getIntExtra(ARG_TAB_SHORTCUT, 0);
                MenuItem menuItem = navigationView.getMenu().getItem(position);
                selectDrawerItem(menuItem, NavigationSource.INITIALISATION);
            } else {
                // Get start position & select it
                int start = getIntent().getIntExtra(ARG_TAB, R.id.drawer_feed);
                selectDrawerItem(navigationView.getMenu().findItem(start), NavigationSource.INITIALISATION);
            }
        } else { //Update title, since this is not saved apparently.
            //Current fragment
            FragmentManager manager = getSupportFragmentManager();
            Fragment current = manager.findFragmentById(R.id.content);
            setTitle(navigationView.getMenu().findItem(getFragmentMenuId(current)).getTitle());
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
     * @param navigationSource Where the navigation originates from.
     */
    private void selectDrawerItem(MenuItem menuItem, @NavigationSource int navigationSource) {

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

        // Set the ID.
        setArguments(fragment, menuItem.getItemId());

        // We use a back stack for the fragments. When a new fragment is shown, we insert it to the back stack.
        // If the fragment is already in the back stack, we restore the back stack to that fragment.
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Current fragment
        Fragment current = fragmentManager.findFragmentById(R.id.content);

        //If this is the same fragment, don't do anything.
        if (current != null && current.getClass().equals(fragment.getClass())) {
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
            this.scheduledContentUpdate = new DrawerUpdate(navigationSource, fragment, menuItem);
        } else {
            Log.d(TAG, "selectDrawerItem: drawer is closed, so doing update now.");
            setFragment(fragment, menuItem, navigationSource);
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

    /**
     * Set the current displayed fragment. This method is executed immediately.
     *
     * @param fragment The new fragment.
     * @param menuItem The menu item corresponding to the fragment.
     * @param navigationSource Where this navigation originates. This determines the behaviour with the back stack.
     */
    private void setFragment(Fragment fragment, MenuItem menuItem, @NavigationSource int navigationSource) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // The name for the back stack and the fragment itself.
        String name = String.valueOf(menuItem.getItemId());

        // If this is a drawer navigation, clear the back stack.
        if (navigationSource == NavigationSource.DRAWER) {
            Log.d(TAG, "setFragment: Clearing back stack, due to drawer navigation.");
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Create the base transaction.
        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment, name);

        // If this is an inner navigation, add it to the back stack.
        if (navigationSource == NavigationSource.INNER) {
            Log.d(TAG, "setFragment: registering on back stack, due to inner navigation.");
            transaction.addToBackStack(name);
        }

        // TODO: temp debug
        if (navigationSource == NavigationSource.INITIALISATION) {
            Log.d(TAG, "setFragment: setting fragment for initialisation");
        }

        // We allow state loss to prevent crashes in rare case.
        transaction.commitAllowingStateLoss();

        // Hide the loader.
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

        // The super method handles both the fragment back stack and finishing the activity. To know what was executed,
        // we need to add a listener.

        FragmentManager.OnBackStackChangedListener listener = () -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.content);
            Log.w(TAG, "onBackPressed: current fragment is somehow null? Ignoring update for now.");
            if (current == null) {
                return;
            }
            MenuItem item = navigationView.getMenu().findItem(getFragmentMenuId(current));
            updateDrawer(item);
        };
        // We need to listen to the back stack to update the drawer.
        getSupportFragmentManager().addOnBackStackChangedListener(listener);
        super.onBackPressed();
        // The drawer has been updated, so we abandon the listener.
        getSupportFragmentManager().removeOnBackStackChangedListener(listener);
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
            setIntent(intent);
            int start = intent.getIntExtra(ARG_TAB, R.id.drawer_feed);
            selectDrawerItem(navigationView.getMenu().findItem(start), NavigationSource.INNER);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectDrawerItem(item, NavigationSource.DRAWER);
        return true;
    }

    /**
     * Represents the origins of a navigation event. These are ints instead of enums for performance reasons.
     * See the class documentation for an overview.
     *
     * TODO: check if should these be enums, proguard would optimize this or not.
     */
    @IntDef({NavigationSource.DRAWER, NavigationSource.INNER, NavigationSource.INITIALISATION})
    private @interface NavigationSource {
        /**
         * The navigation originates from the drawer. Represents scenario 1.
         */
        int DRAWER = 0;
        /**
         * The navigation originates from an user action inside the fragments. Represents scenario 2.
         */
        int INNER = 1;
        /**
         * The navigation originates from the activity being initialised. Represents scenario 3.
         */
        int INITIALISATION = 2;
    }

    /**
     * Groups an update for the navigation drawer.
     */
    private static class DrawerUpdate {
        @NavigationSource
        final int navigationSource;
        final Fragment fragment;
        final MenuItem menuItem;

        private DrawerUpdate(int navigationSource, Fragment fragment, MenuItem menuItem) {
            this.navigationSource = navigationSource;
            this.fragment = fragment;
            this.menuItem = menuItem;
        }
    }

    /**
     * Allow fragments to extract arguments from the activity.
     *
     * @author Niko Strijbol
     */
    @FunctionalInterface
    public interface ArgumentsReceiver {

        /**
         * Called when the fragment is created by the hosting activity. Allows the fragment to extract arguments from
         * the {@code activityIntent} and put them in {@code existingArguments}. The resulting bundle will then
         * eventually be set as the arguments of the fragment.
         *
         * This function should be a pure function, meaning there should be no side effects in the fragment. Side-effects
         * resulting from this function may cause undefined behaviour.
         *
         * @param activityIntent The intent of the activity.
         * @param existingArguments The bundle to put the arguments in.
         */
        void fillArguments(Intent activityIntent, Bundle existingArguments);
    }

    /**
     * Used to notify fragments they will be removed. At this point, a fragment should consider itself removed,
     * even if it is not yet removed.
     *
     * This allows fragments to do things like dismiss snackbars, or close action modes.
     *
     * @author Niko Strijbol
     */
    @FunctionalInterface
    public interface ScheduledRemovalListener {

        /**
         * Called when the user will be switching to another fragment.
         */
        void onRemovalScheduled();
    }
}