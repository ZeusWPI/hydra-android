package be.ugent.zeus.hydra.common.reporting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.BuildConfig;

/**
 * Produces the default tracker.
 *
 * @author Niko Strijbol
 */
public final class Reporting {

    private static final String TAG = "Reporting";

    private static final String PREF_ALLOW_ANALYTICS = "be.ugent.zeus.hydra.reporting.allow_analytics";
    private static final String PREF_ALLOW_CRASH_REPORTING = "be.ugent.zeus.hydra.reporting.allow_crash_reporting";

    private static Tracker tracker;
    private final static Object lock = new Object();

    /**
     * Get the default tracker.
     *
     * @param context The context.
     *
     * @return The tracker.
     */
    public static Tracker getTracker(Context context) {
        synchronized (lock) {
            if (tracker == null) {
                tracker = new FirebaseTracker(context);
            }
        }
        return tracker;
    }

    public static BaseEvents getEvents() {
        return FirebaseEvents.getInstance();
    }

    public static void saveAnalyticsPermission(Context context, boolean allowed) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putBoolean(PREF_ALLOW_ANALYTICS, allowed)
                .apply();
    }

    public static void saveCrashReportingPermission(Context context, boolean allowed) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putBoolean(PREF_ALLOW_CRASH_REPORTING, allowed)
                .apply();
    }

    /**
     * Sync the collecting of data with the user preference and the build type. By default, analytics
     * is opt-in, while crash reporting is opt-out.
     *
     * Additionally, both analytics and crash reporting will be disabled on debug builds.
     *
     * @param context The context.
     */
    public static void syncPermissions(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Tracker tracker = getTracker(context);

        // Enable or disable analytics
        boolean areAnalyticsAllowed = preferences.getBoolean(PREF_ALLOW_ANALYTICS, false) && allowDebugReporting();
        tracker.allowAnalytics(areAnalyticsAllowed);
        Log.i(TAG, "permissions: allowing analytics? " + areAnalyticsAllowed);

        // Enable or disable crash reporting
        boolean isCrashReportingAllowed = preferences.getBoolean(PREF_ALLOW_CRASH_REPORTING, true) && allowDebugReporting();
        tracker.allowCrashReporting(isCrashReportingAllowed);
        Log.i(TAG, "permissions: allowing crash reporting? " + isCrashReportingAllowed);
    }

    public static boolean allowDebugReporting() {
        // If a DEBUG build, use the property, otherwise OK!
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "allowDebugReporting: this is debug mode");
            return BuildConfig.DEBUG_ENABLE_REPORTING;
        } else {
            return true;
        }
    }
}
