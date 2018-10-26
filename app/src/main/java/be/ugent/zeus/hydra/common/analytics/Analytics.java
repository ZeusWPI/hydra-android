package be.ugent.zeus.hydra.common.analytics;

import android.content.Context;

/**
 * Produces the default tracker.
 *
 * @author Niko Strijbol
 */
public final class Analytics {

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
}
