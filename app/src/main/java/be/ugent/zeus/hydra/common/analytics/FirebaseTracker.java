package be.ugent.zeus.hydra.common.analytics;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * @author Niko Strijbol
 */
class FirebaseTracker implements Tracker {

    private final FirebaseAnalytics analytics;

    FirebaseTracker(Context context) {
        this.analytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void log(Event event) {
        if (event.getEventName() != null) {
            analytics.logEvent(event.getEventName(), event.getParams());
        }
    }

    @Override
    public void setCurrentScreen(@NonNull Activity activity, String screenName, String classOverride) {
        analytics.setCurrentScreen(activity, screenName, classOverride);
    }
}
