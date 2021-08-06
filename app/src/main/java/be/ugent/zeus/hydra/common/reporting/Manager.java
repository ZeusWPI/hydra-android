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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.BuildConfig;

/**
 * Manage reporting permissions.
 *
 * @author Niko Strijbol
 */
public class Manager {

    private static final String TAG = "Reporting";
    private static final String PREF_ALLOW_ANALYTICS = "be.ugent.zeus.hydra.reporting.allow_analytics";
    private static final String PREF_ALLOW_CRASH_REPORTING = "be.ugent.zeus.hydra.reporting.allow_crash_reporting";

    public static void saveAnalyticsPermission(Context context, boolean allowed) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putBoolean(PREF_ALLOW_ANALYTICS, allowed)
                .apply();
    }

    public static void saveCrashReportingPermission(Context context, boolean allowed) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putBoolean(PREF_ALLOW_CRASH_REPORTING, allowed)
                .apply();
    }

    /**
     * Sync the collecting of data with the user preference and the build type. By default, analytics
     * is opt-in, while crash reporting is opt-out.
     * <p>
     * Additionally, both analytics and crash reporting will be disabled on debug builds.
     *
     * @param context The context.
     */
    public static void syncPermissions(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Tracker tracker = Reporting.getTracker(context);

        // Enable or disable analytics
        boolean areAnalyticsAllowed = preferences.getBoolean(PREF_ALLOW_ANALYTICS, false) && allowDebugReporting();
        tracker.allowAnalytics(areAnalyticsAllowed);
        Log.i(TAG, "permissions: allowing analytics? " + areAnalyticsAllowed);

        // Enable or disable crash reporting
        boolean isCrashReportingAllowed = preferences.getBoolean(PREF_ALLOW_CRASH_REPORTING, true) && allowDebugReporting();
        tracker.allowCrashReporting(isCrashReportingAllowed);
        Log.i(TAG, "permissions: allowing crash reporting? " + isCrashReportingAllowed);
    }

    public static boolean allowDebugReporting() {
        return BuildConfig.DEBUG_ENABLE_REPORTING;
    }
}
