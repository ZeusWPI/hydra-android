package be.ugent.zeus.hydra.common.analytics;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * Provides the basic method for logging analytics events.
 *
 * You should get an instance of the tracker from the {@link AnalyticsFactory}. Normally, the implementation will track
 * certain things automatically. For example, Firebase automatically tracks:
 * <ul>
 * <li>Activities as screens and activity changes</li>
 * <li>Certain user properties</li>
 * </ul>
 *
 * @author Niko Strijbol
 */
public interface Tracker {

    /**
     * Log an event.
     *
     * @param event      The event to log. The string should be known to the implementation of the tracker, e.g. using
     *                   known Firebase events.
     * @param parameters The parameters. These should follow the requirements of the tracker implementation.
     */
    void log(String event, Bundle parameters);

    /**
     * Log an event.
     *
     * @param event The event to log. The data of the event should follow the requirements of the tracker
     *              implementation.
     */
    void log(Event event);

    /**
     * Set the current screen. The current screen remains until the activity changes or this method is called. Should
     * you change this, onResume is a good place to do so.
     *
     * @param activity      The current activity.
     * @param screenName    The name of the screen.
     * @param classOverride The name of the screen class. By default, this is the current activity.
     */
    @MainThread
    void setCurrentScreen(@NonNull Activity activity, String screenName, String classOverride);

    /**
     * Set a user property.
     *
     * @param property The name of the property.
     * @param value    The value of the property.
     */
    void setUserProperty(String property, String value);
}
