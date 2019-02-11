package be.ugent.zeus.hydra;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import be.ugent.zeus.hydra.common.ChannelCreator;
import be.ugent.zeus.hydra.common.preferences.ThemeFragment;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.Tracker;
import jonathanfinerty.once.Once;

/**
 * The Hydra application.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
@SuppressWarnings("WeakerAccess")
public class HydraApplication extends Application {

    private static final String TAG = "HydraApplication";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        onAttachBaseContextInitialize(base);
    }

    /**
     * This method allows us to override this in Robolectric.
     */
    protected void onAttachBaseContextInitialize(Context base) {
        if (BuildConfig.DEBUG) {
            MultiDex.install(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onCreateInitialise();
    }

    /**
     * This method allows us to override this in Robolectric.
     */
    protected void onCreateInitialise() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        if (BuildConfig.DEBUG) {
            enableStrictModeInDebug();
        }

        // Enable or disable analytics.
        Reporting.syncPermissions(this);

        // Set the theme.
        AppCompatDelegate.setDefaultNightMode(ThemeFragment.getNightMode(this));
        trackTheme();

        AndroidThreeTen.init(this);
        LeakCanary.install(this);
        Once.initialise(this);

        // Initialize the channels that are needed in the whole app. The channels for Minerva are created when needed.
        createChannels();
    }

    private void trackTheme() {
        Tracker tracker = Reporting.getTracker(this);
        switch (ThemeFragment.getNightMode(this)) {
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                tracker.setUserProperty("theme", "auto");
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                tracker.setUserProperty("theme", "follow system");
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                tracker.setUserProperty("theme", "dark");
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                tracker.setUserProperty("theme", "light");
                break;
        }
    }

    /**
     * Create notifications channels when needed.
     * TODO: should this move to the SKO activity?
     */
    protected void createChannels() {
        ChannelCreator channelCreator = ChannelCreator.getInstance(this);
        channelCreator.createSkoChannel();
    }

    /**
     * Used to enable {@link StrictMode} for debug builds.
     */
    protected static void enableStrictModeInDebug() {

        if (!BuildConfig.DEBUG || !BuildConfig.DEBUG_ENABLE_STRICT_MODE) {
            return;
        }

        Log.d(TAG, "Enabling strict mode...");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }
}