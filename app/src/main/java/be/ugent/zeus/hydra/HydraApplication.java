package be.ugent.zeus.hydra;

import android.app.Activity;
import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import be.ugent.zeus.hydra.common.ChannelCreator;
import be.ugent.zeus.hydra.theme.ThemePreferenceFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;
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
    public void onCreate() {
        super.onCreate();
        onCreateInitialise();
    }

    protected void onCreateInitialise() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        if (BuildConfig.DEBUG) {
            enableStrictModeInDebug();
        }

        // Set the theme.
        AppCompatDelegate.setDefaultNightMode(ThemePreferenceFragment.getNightMode(this));

        AndroidThreeTen.init(this);
        LeakCanary.install(this);
        Once.initialise(this);

        // Initialize the channels that are needed in the whole app. The channels for Minerva are created when needed.
        createChannels();
    }

    /**
     * Send a screen name to the analytics.
     *
     * @param screenName The screen name to send.
     */
    @MainThread
    public static void sendScreenName(@Nullable Activity activity, @NonNull String screenName) {
        if (activity == null) {
            return;
        }
        FirebaseAnalytics.getInstance(activity).setCurrentScreen(activity, screenName, null);
    }

    /**
     * Get the application from an activity. The application is cast to this class.
     *
     * @param activity The activity.
     *
     * @return The application.
     */
    public static HydraApplication getApplication(@NonNull Activity activity) {
        return (HydraApplication) activity.getApplication();
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

        if (!BuildConfig.DEBUG_ENABLE_STRICT_MODE) {
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