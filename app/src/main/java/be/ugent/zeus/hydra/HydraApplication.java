package be.ugent.zeus.hydra;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;

import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.Tracker;
import be.ugent.zeus.hydra.preferences.ThemeFragment;
import com.jakewharton.threetenabp.AndroidThreeTen;
import jonathanfinerty.once.Once;

/**
 * The Hydra application.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
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
            androidx.multidex.MultiDex.install(this);
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
        if (BuildConfig.DEBUG) {
            enableStrictModeInDebug();
        }

        // Enable or disable analytics.
        Reporting.syncPermissions(this);

        // Set the theme.
        AppCompatDelegate.setDefaultNightMode(ThemeFragment.getNightMode(this));
        trackTheme();

        AndroidThreeTen.init(this);
        Once.initialise(this);
    }

    @SuppressLint("SwitchIntDef")
    private void trackTheme() {
        Tracker tracker = Reporting.getTracker(this);
        switch (ThemeFragment.getNightMode(this)) {
            case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
                tracker.setUserProperty("theme", "battery");
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
     * Used to enable {@link StrictMode} for debug builds.
     */
    private static void enableStrictModeInDebug() {
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
