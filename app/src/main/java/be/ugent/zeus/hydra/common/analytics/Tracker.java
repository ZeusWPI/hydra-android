package be.ugent.zeus.hydra.common.analytics;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Provides the basic method for logging analytics events.
 *
 * You should get an instance of the tracker from the {@link Analytics}. Normally, the implementation will track certain
 * things automatically. For example, Firebase automatically tracks:
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
     * Set a user property. The implementation might reserve some property names.
     *
     * @param name  The name of the property.
     * @param value The value.
     */
    void setUserProperty(String name, String value);
}
