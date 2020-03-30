package be.ugent.zeus.hydra.common.reporting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import androidx.preference.PreferenceManager;
import be.ugent.zeus.hydra.BuildConfig;

/**
 * Produces the default tracker.
 *
 * @author Niko Strijbol
 */
public final class Reporting {

    private static Tracker tracker;
    private final static Object lock = new Object();

    /**
     * Get the default tracker.
     *
     * @param context The context.
     * @return The tracker.
     */
    public static Tracker getTracker(Context context) {
        synchronized (lock) {
            if (tracker == null) {
                tracker = new DummyTracker();
            }
        }
        return tracker;
    }

    public static BaseEvents getEvents() {
        return new DummyHolder();
    }

    @VisibleForTesting
    public static void setTracker(Tracker tracker) {
        Reporting.tracker = tracker;
    }

    public static boolean hasReportingOptions() {
        return false;
    }
}
