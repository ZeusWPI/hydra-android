package be.ugent.zeus.hydra.common.reporting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

/**
 * A tracker implemented using Firebase Analytics and Crashlytics.
 *
 * @author Niko Strijbol
 */
class FirebaseTracker implements Tracker {

    private final Context applicationContext;
    private boolean isCrashReportingAllowed = true;

    FirebaseTracker(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public void log(Event event) {
        if (event.getEventName() != null) {
            FirebaseAnalytics.getInstance(applicationContext)
                    .logEvent(event.getEventName(), event.getParams());
        }
    }

    @Override
    public void setCurrentScreen(@NonNull Activity activity, String screenName, String classOverride) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, classOverride);
        FirebaseAnalytics.getInstance(applicationContext)
                .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    @Override
    public void setUserProperty(String name, String value) {
        FirebaseAnalytics.getInstance(applicationContext)
                .setUserProperty(name, value);
    }

    @Override
    public void logError(Throwable throwable) {
        // Prevent logging exceptions if it is not enabled.
        if (isCrashReportingAllowed) {
            FirebaseCrashlytics.getInstance().recordException(throwable);
        }
    }

    @Override
    public void allowAnalytics(boolean allowed) {
        if (allowed) {
            FirebaseAnalytics.getInstance(applicationContext)
                    .setAnalyticsCollectionEnabled(true);
        }
    }

    @Override
    public void allowCrashReporting(boolean allowed) {
        isCrashReportingAllowed = allowed;
        if (allowed) {
            FirebaseApp.initializeApp(this.applicationContext);
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        }
    }
}