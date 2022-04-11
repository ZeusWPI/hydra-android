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

package be.ugent.zeus.hydra;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.StrictMode;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import be.ugent.zeus.hydra.common.reporting.Manager;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.Tracker;
import be.ugent.zeus.hydra.preferences.ThemeFragment;
import jonathanfinerty.once.Once;

/**
 * The Hydra application.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class HydraApplication extends Application {

    private static final String TAG = "HydraApplication";

    /**
     * Used to enable {@link StrictMode} for debug builds.
     */
    private static void enableStrictModeInDebug() {
        if (!BuildConfig.DEBUG || !BuildConfig.DEBUG_ENABLE_STRICT_MODE) {
            return;
        }

        Log.d(TAG, "Enabling strict mode...");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onCreateInitialise();
    }

    /**
     * This method allows us to override this in Robolectric.
     */
    protected void onCreateInitialise() {
        if (BuildConfig.DEBUG) {
            enableStrictModeInDebug();
        }

        // Enable or disable analytics.
        Manager.syncPermissions(this);

        // Set the theme.
        AppCompatDelegate.setDefaultNightMode(ThemeFragment.getNightMode(this));
        trackTheme();
        
        // Allow dynamic colours
        DynamicColors.applyToActivitiesIfAvailable(this);

        Once.initialise(this);
    }

    @SuppressLint("SwitchIntDef")
    private void trackTheme() {
        Tracker tracker = Reporting.getTracker(this);
        switch (ThemeFragment.getNightMode(this)) {
            case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
                tracker.setUserProperty("theme", "battery");
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                tracker.setUserProperty("theme", "follow system");
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                tracker.setUserProperty("theme", "dark");
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                tracker.setUserProperty("theme", "light");
                break;
        }
    }
}
