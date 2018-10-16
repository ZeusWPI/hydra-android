package be.ugent.zeus.hydra.common.analytics;

import android.content.Context;

/**
 * @author Niko Strijbol
 */
public class AnalyticsFactory {

    private static Tracker tracker;
    private final static Object lock = new Object();

    public Tracker getTracker(Context context) {
        synchronized (lock) {
            if (tracker == null) {
                tracker = new FirebaseTracker(context);
            }
        }
        return tracker;
    }
}
