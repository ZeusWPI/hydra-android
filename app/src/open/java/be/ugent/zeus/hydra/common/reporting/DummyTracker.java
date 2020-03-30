package be.ugent.zeus.hydra.common.reporting;

import android.app.Activity;
import androidx.annotation.NonNull;

/**
 * Tracker that does nothing.
 *
 * @author Niko Strijbol
 */
class DummyTracker implements Tracker {
    @Override
    public void log(Event event) {
        // Nothing.
    }

    @Override
    public void setCurrentScreen(@NonNull Activity activity, String screenName, String classOverride) {
        // Nothing.
    }

    @Override
    public void setUserProperty(String name, String value) {
        // Nothing.
    }

    @Override
    public void logError(Throwable throwable) {
        // Nothing.
    }

    @Override
    public void logErrorMessage(String message) {
        // Nothing.
    }

    @Override
    public void allowAnalytics(boolean allowed) {
        // Nothing.
    }

    @Override
    public void allowCrashReporting(boolean allowed) {
        // Nothing.
    }
}
