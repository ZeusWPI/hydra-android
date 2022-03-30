/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra;

import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.*;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import be.ugent.zeus.hydra.association.list.EventFragment;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityMainBinding;
import be.ugent.zeus.hydra.feed.HomeFeedFragment;
import be.ugent.zeus.hydra.info.InfoFragment;
import be.ugent.zeus.hydra.library.list.LibraryListFragment;
import be.ugent.zeus.hydra.news.NewsFragment;
import be.ugent.zeus.hydra.onboarding.OnboardingActivity;
import be.ugent.zeus.hydra.preferences.PreferenceActivity;
import be.ugent.zeus.hydra.resto.menu.RestoFragment;
import be.ugent.zeus.hydra.schamper.SchamperFragment;
import be.ugent.zeus.hydra.urgent.UrgentFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.tabs.TabLayout;
import dev.chrisbanes.insetter.Insetter;
import jonathanfinerty.once.Once;

import static be.ugent.zeus.hydra.common.utils.FragmentUtils.requireArguments;

/**
 * Main activity.
 * <p>
 * The logic for handling the navigation drawer is not immediately obvious to those who do not work with it regularly.
 * Proceed with caution.
 *
 * <h1>Navigation</h1>
 * <p>
 * One of the main responsibilities of this activity is managing navigation between the drawer, the fragments and the
 * fragments between each other. The navigation is built in two main components: navigation forward and navigating
 * backwards. Each component itself is not that difficult. Together the provide an intuitive navigation.
 *
 * <h2>Forward navigation</h2>
 * <p>
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
 * <p>
 * On the first scenario, the back stack should be cleared [1]. The new fragment should not be added to the back stack,
 * as it should not be reversed. An implementation complexity is that this should not happen when restoring the
 * activity (scenario three).
 * <p>
 * In the second scenario, the user probably expects to be able to go back. Therefore, these should be added to the
 * back stack. Currently it is unlikely this will cause back stacks that are too large. In the future, we might need
 * to restrict this, e.g. by checking for existing instances of a fragment in the stack and popping until that instance
 * is reached. For example, assume the back stack is B -> A -> C. When the user then navigates to fragment A, the C
 * fragment should be removed from the stack.
 * <p>
 * The third and last scenario is the easiest: nothing should be done when the activity is first started or recreated.
 * The fragments should not be added to the back stack.
 *
 * <h2>Backwards navigation</h2>
 * <p>
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
 * +------v----------------+  No    +-----------------------+
 * |  Is back stack empty? +-------->  Pop from back stack  |
 * +------+----------------+        +-----------------------+
 *        |
 *        | Yes
 *        |
 * +------v----------------+
 * |  Let activity finish  |
 * +-----------------------+
 * }</pre>
 *
 * <h1>Arguments for children</h1>
 * <p>
 * If a child fragment needs arguments to initialise, it should implement {@link ArgumentsReceiver}. When implementing
 * this interface, the activity will call this method on the fragment. This gives the fragment a chance to extract
 * arguments from the intent of the activity, which will then be set as arguments to the fragment.
 * <p>
 * After this method call, the fragment can behave as if the arguments were directly set on the fragment itself.
 * <p>
 * This function will only be called when creating a fragment, not when popping from the back stack.
 *
 * <h1>Common views and removal</h1>
 * <p>
 * The activity provides some common views:
 * <ul>
 *     <li>A {@link TabLayout} with id {@link R.id#tab_layout}</li>
 *     <li>A {@link BottomNavigationView} with id {@link R.id#bottom_navigation}</li>
 * </ul>
 * <p>
 * The fragment's view is automatically adjusted for the TabLayout, if the fragment makes it visible. The activity
 * does not manage the BottomNavigationView however. Fragments must take care of that themselves. The recommended
 * method is adding X bottom padding to the main view, where X is the height of the BottomNavigationView.
 * <p>
 * These are hidden by default and should be shown and hidden by the fragments that use them.
 * <p>
 * To this end, a fragment may implement {@link ScheduledRemovalListener}. This callback will called when the activity
 * determines that a fragment is no longer in use and fragment-specific views should be hidden.
 * <p>
 * The fragment itself should not be hidden; the activity takes care of this. The callback is specifically for views
 * outside the normal fragment, such as the two above or a contextual action bar.
 * <p>
 * The reason fragments cannot fully rely on the default lifecycle methods, such as {@link Fragment#onStop()}, is that
 * the fragment is not always removed immediately by the activity when it is hidden (this as to do with performance).
 *
 * <h1>Arguments</h1>
 * <p>
 * The activity has one public argument: which child fragment to load. The preferred way of using it is
 * {@link #ARG_TAB}, with an int value. The int value is the ID of the menu item in the drawer.
 *
 * @see [1] <a href="https://youtu.be/Sww4omntVjs?t=13m46s">Android Design in Action: Navigation Anti-Patterns, pattern 6</a>
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_TAB = "argTab";
    @SuppressWarnings("WeakerAccess")
    public static final String ARG_TAB_SHORTCUT = "argTabShortcut";
    @VisibleForTesting
    static final String ONCE_ONBOARDING = "once_onboarding_v1";
    @VisibleForTesting
    static final String ONCE_DRAWER = "once_drawer";
    private static final String TAG = "BaseActivity";
    private static final String UFORA = "com.d2l.brightspace.student.android";
    private static final int ONBOARDING_REQUEST = 5;
    private static final String STATE_IS_ONBOARDING_OPEN = "state_is_onboarding_open";
    private static final String FRAGMENT_MENU_ID = "backStack";

    private static final String SHORTCUT_RESTO = "resto";
    private static final String SHORTCUT_URGENT = "urgent";
    private static final String SHORTCUT_EVENTS = "events";
    private static final String SHORTCUT_LIBRARIES = "libraries";

    private ActionBarDrawerToggle toggle;

    private boolean isOnboardingOpen;

    /**
     * Contains the next fragment. This fragment will be shown when the navigation drawer has been closed, to prevent
     * lag. If null, nothing should happen on close.
     */
    @Nullable
    private DrawerUpdate scheduledContentUpdate;

    /**
     * Get the position of a fragment in the menu. While stupid, there is no other way to do this.
     */
    @IdRes
    private static int getFragmentMenuId(Fragment fragment) {
        return requireArguments(fragment).getInt(FRAGMENT_MENU_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityMainBinding::inflate);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        // Prevent toolbar appearing behind notification bar.
        this.binding.appBarLayout.setStatusBarForeground(MaterialShapeDrawable.createWithElevationOverlay(this));
        // Prevent navigation drawer from extending behind the notification bar.
        // It doesn't work to just set fitsSystemWindows=true, since this adds the padding
        // for the navigation bar to the header, which is not what you want.
        Insetter.builder()
                .padding(WindowInsetsCompat.Type.statusBars())
                .applyToView(this.binding.navigationView.getHeaderView(0));

        if (savedInstanceState != null) {
            isOnboardingOpen = savedInstanceState.getBoolean(STATE_IS_ONBOARDING_OPEN, false);
        }

        // Show onboarding if the user has not completed it yet.
        if (!Once.beenDone(ONCE_ONBOARDING) && !isOnboardingOpen) {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivityForResult(intent, ONBOARDING_REQUEST);
            isOnboardingOpen = true;
        }

        initialize(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_ONBOARDING_OPEN, isOnboardingOpen);
    }

    private void initialize(@Nullable Bundle savedInstanceState) {

        // Register the listener for navigation events from the drawer.
        binding.navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.action_drawer_open, R.string.action_drawer_close) {
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
        binding.drawerLayout.addDrawerListener(toggle);
        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
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
                MenuItem menuItem = binding.navigationView.getMenu().getItem(position);
                selectDrawerItem(menuItem, NavigationSource.INITIALISATION);
            } else {
                // Get start position & select it
                int start = getIntent().getIntExtra(ARG_TAB, R.id.drawer_feed);
                selectDrawerItem(binding.navigationView.getMenu().findItem(start), NavigationSource.INITIALISATION);
            }
        } else { //Update title, since this is not saved apparently.
            //Current fragment
            FragmentManager manager = getSupportFragmentManager();
            Fragment current = manager.findFragmentById(R.id.content);
            setTitle(binding.navigationView.getMenu().findItem(getFragmentMenuId(current)).getTitle());
        }

        // If this is the first time, open the drawer.
        if (!Once.beenDone(ONCE_DRAWER)) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Select a drawer item. This will update the fragment to the new one from the menu item, if it is a different one
     * than the current one.
     * <p>
     * Note: this function does not update the drawer if the fragment should be adjusted, only if another activity
     * should be opened.
     *
     * @param menuItem         The item to display.
     * @param navigationSource Where the navigation originates from.
     */
    private void selectDrawerItem(@NonNull MenuItem menuItem, @NavigationSource int navigationSource) {

        // First check if it are settings, then we don't update anything.
        if (menuItem.getItemId() == R.id.drawer_pref) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            PreferenceActivity.start(this, null);
            return;
        }

        if (menuItem.getItemId() == R.id.drawer_ufora) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(UFORA);
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + UFORA));
                intent.setPackage("com.android.vending");
                startActivity(intent);
            }
            return; // Don't do anything.
        }

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment;
        int itemId = menuItem.getItemId();
        if (itemId == R.id.drawer_feed) {
            fragment = new HomeFeedFragment();
        } else if (itemId == R.id.drawer_schamper) {
            fragment = new SchamperFragment();
        } else if (itemId == R.id.drawer_resto) {
            reportShortcutUsed(SHORTCUT_RESTO);
            fragment = new RestoFragment();
        } else if (itemId == R.id.drawer_events) {
            reportShortcutUsed(SHORTCUT_EVENTS);
            fragment = new EventFragment();
        } else if (itemId == R.id.drawer_news) {
            fragment = new NewsFragment();
        } else if (itemId == R.id.drawer_info) {
            fragment = new InfoFragment();
        } else if (itemId == R.id.drawer_urgent) {
            reportShortcutUsed(SHORTCUT_URGENT);
            fragment = new UrgentFragment();
        } else if (itemId == R.id.drawer_library) {
            reportShortcutUsed(SHORTCUT_LIBRARIES);
            fragment = new LibraryListFragment();
        } else {
            throw new IllegalStateException("Unknown menu id for navigation drawer");
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
        binding.appBarLayout.setExpanded(true);

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Hide the current fragment now, similar to how GMail handles things.
            if (current != null && current.getView() != null) {
                current.getView().setVisibility(View.GONE);
                binding.progressBar.progressBar.setVisibility(View.VISIBLE);
            }
            // Since there will be a delay, notify the fragment to prevent lingering snackbars or action modes.
            if (current instanceof ScheduledRemovalListener) {
                ((ScheduledRemovalListener) current).onRemovalScheduled();
            }
            this.scheduledContentUpdate = new DrawerUpdate(navigationSource, fragment, menuItem);
        } else {
            setFragment(fragment, menuItem, navigationSource);
        }
        updateDrawer(fragment, menuItem);
    }

    /**
     * Manually set the drawer item, and close the drawers. This will not update the fragment.
     *
     * @param item The item to mark as current.
     */
    private void updateDrawer(Fragment fragment, MenuItem item) {
        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Log the screen in the Reporting.
        Reporting.getTracker(this)
                .setCurrentScreen(this, item.getTitle().toString(), fragment.getClass().getSimpleName());
        // Close the navigation drawer
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Set the current displayed fragment. This method is executed immediately.
     *
     * @param fragment         The new fragment.
     * @param menuItem         The menu item corresponding to the fragment.
     * @param navigationSource Where this navigation originates. This determines the behaviour with the back stack.
     */
    private void setFragment(Fragment fragment, MenuItem menuItem, @NavigationSource int navigationSource) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // The name for the back stack and the fragment itself.
        String name = String.valueOf(menuItem.getItemId());

        // If this is a drawer navigation, clear the back stack.
        if (navigationSource == NavigationSource.DRAWER) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Create the base transaction.
        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment, name);

        // If this is an inner navigation, add it to the back stack.
        if (navigationSource == NavigationSource.INNER) {
            transaction.addToBackStack(name);
        }

        // We allow state loss to prevent crashes in rare case.
        transaction.commitAllowingStateLoss();

        // Hide the loader.
        binding.progressBar.progressBar.setVisibility(View.GONE);
    }

    /**
     * Implements the correct back-button behaviour for the drawer. If the drawer does not consume the back press,
     * the parent method is called.
     */
    @Override
    public void onBackPressed() {
        // If the drawer is open, close it.
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
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
            MenuItem item = binding.navigationView.getMenu().findItem(getFragmentMenuId(current));
            updateDrawer(current, item);
        };

        // Allow the current fragment to intercept the back press.
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.content);
        if (current instanceof OnBackPressed && (((OnBackPressed) current).onBackPressed())) {
            // The fragment has handled it.
            return;
        }

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
     * @param id       The id.
     */
    private void setArguments(Fragment fragment, @IdRes int id) {
        Bundle arguments = new Bundle();
        arguments.putInt(FRAGMENT_MENU_ID, id);
        if (fragment instanceof ArgumentsReceiver) {
            ((ArgumentsReceiver) fragment).fillArguments(getIntent(), arguments);
        }
        fragment.setArguments(arguments);
    }

    @Override
    protected boolean hasParent() {
        return false;
    }

    private void reportShortcutUsed(String shortcutId) {
        if (android.os.Build.VERSION.SDK_INT >= 25) {
            ShortcutManager manager = Objects.requireNonNull(getSystemService(ShortcutManager.class));
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
                isOnboardingOpen = false;
                // Log sign in
                Reporting.getTracker(this)
                        .log(new TutorialEndEvent());
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
            selectDrawerItem(binding.navigationView.getMenu().findItem(start), NavigationSource.INNER);
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
         * <p>
         * This function should be a pure function, meaning there should be no side effects in the fragment. Side-effects
         * resulting from this function may cause undefined behaviour.
         *
         * @param activityIntent    The intent of the activity.
         * @param existingArguments The bundle to put the arguments in.
         */
        void fillArguments(Intent activityIntent, Bundle existingArguments);
    }

    /**
     * Used to notify fragments they will be removed. At this point, a fragment should consider itself removed,
     * even if it is not yet removed.
     * <p>
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

    /**
     * Allows fragments to listen to and intercept back button presses.
     */
    @FunctionalInterface
    public interface OnBackPressed {

        /**
         * Called when the back button is pressed. This function provides the fragment
         * with an opportunity to intercept the back press.
         *
         * @return True if consumed, false otherwise. Consumed events are not propagated.
         */
        boolean onBackPressed();
    }

    private static final class TutorialEndEvent implements Event {
        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().tutorialComplete();
        }
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
}
