package be.ugent.zeus.hydra.sko;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.sko.preferences.SkoPreferencesFragment;
import be.ugent.zeus.hydra.sko.preferences.WrapperActivity;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * SKO overview activity. Displays the 4 tabs to the user. The tabs are provided by the {@link PagerAdapter}.
 *
 * @author Niko Strijbol
 */
public class OverviewActivity extends BaseActivity {

    private static final String SKO_WEBSITE = Endpoints.SKO;

    private static final String TAG = "SkoOverviewActivity";

    /**
     * Argument specifying the tab position.
     */
    private static final String ARG_TAB = "argTab";
    /**
     * The default value for  {@link #ARG_TAB}.
     */
    private static final int DEFAULT_TAB_POSITION = 0;

    /**
     * Start the activity on the default tab.
     *
     * @param context The context.
     *
     * @return The intent to start the activity.
     */
    public static Intent start(Context context) {
        return new Intent(context, OverviewActivity.class);
    }

    /**
     * Start the activity at a given position.
     *
     * @param context       The context.
     * @param startPosition The start position. Must be between [0,3].
     *
     * @return The intent to start the activity.
     */
    public static Intent start(Context context, @IntRange(from = 0, to = 3) int startPosition) {
        Intent intent = new Intent(context, OverviewActivity.class);
        intent.putExtra(ARG_TAB, startPosition);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sko_main);

        ViewPager viewpager = findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), this);
        viewpager.setAdapter(adapter);

        final AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        // TODO: log the screens (is this still necessary?).
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewpager);

        //Get start position
        int start = getIntent().getIntExtra(ARG_TAB, DEFAULT_TAB_POSITION);
        viewpager.setCurrentItem(start, false);

        //Subscribe to topic if needed
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains(SkoPreferencesFragment.PREF_SKO_NOTIFICATION)) {
            Log.i(TAG, "Subscribing to notifications.");
            FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessageService.SKO_TOPIC);
            prefs.edit().putBoolean(SkoPreferencesFragment.PREF_SKO_NOTIFICATION, true).apply();
            showNotificationSnackbar();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sko, menu);
        tintToolbarIcons(menu, R.id.sko_visit_website);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sko_visit_website:
                NetworkUtils.maybeLaunchBrowser(this, SKO_WEBSITE);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, WrapperActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNotificationSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.sko_notification_on, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_turn_off, v -> {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseMessageService.SKO_TOPIC);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OverviewActivity.this);
            prefs.edit().putBoolean(SkoPreferencesFragment.PREF_SKO_NOTIFICATION, false).apply();
        });
        snackbar.show();
    }
}