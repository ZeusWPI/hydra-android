package be.ugent.zeus.hydra.common.reporting;

import android.content.Context;
import androidx.annotation.VisibleForTesting;

/**
 * Produces the default tracker.
 *
 * @author Niko Strijbol
 */
public final class Reporting {

    private final static Object lock = new Object();
    private static Tracker tracker;

    /**
     * Get the default tracker.
     *
     * @param context The context.
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

    @VisibleForTesting
    public static void setTracker(Tracker tracker) {
        Reporting.tracker = tracker;
    }

    public static boolean hasReportingOptions() {
        return true;
    }
}
