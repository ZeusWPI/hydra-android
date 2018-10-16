package be.ugent.zeus.hydra.common.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
    public void log(String event, Bundle parameters) {
        analytics.logEvent(event, parameters);
    }

    @Override
    public void log(Event event) {
        analytics.logEvent(event.getEvent(), event.getParams());
    }

    @Override
    public void setCurrentScreen(@NonNull Activity activity, String screenName, String classOverride) {
        analytics.setCurrentScreen(activity, screenName, classOverride);
    }

    @Override
    public void setUserProperty(String property, String value) {
        analytics.setUserProperty(property, value);
    }
}
