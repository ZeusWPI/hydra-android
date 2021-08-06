/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.common.reporting;

import android.app.Activity;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

/**
 * Provides the basic method for logging analytics events.
 * <p>
 * You should get an instance of the tracker from the {@link Reporting}. Normally, the implementation will track certain
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
    @SuppressWarnings("EmptyMethod")
    void setCurrentScreen(@NonNull Activity activity, String screenName, String classOverride);

    /**
     * Set a user property. The implementation might reserve some property names.
     *
     * @param name  The name of the property.
     * @param value The value.
     */
    void setUserProperty(String name, String value);

    /**
     * Log a non-fatal exception to the crash service.
     *
     * @param throwable The exception to log.
     */
    void logError(Throwable throwable);

    /**
     * Configure the underlying service to allow analytics or not.
     *
     * @param allowed If analytics collection is allowed or not.
     */
    void allowAnalytics(boolean allowed);

    /**
     * Configure the underlying crash service to allow crash reporting or not.
     *
     * @param allowed If crash reporting is allowed or not.
     */
    void allowCrashReporting(boolean allowed);
}
