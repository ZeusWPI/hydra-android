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
        if (event.eventName() != null) {
            FirebaseAnalytics.getInstance(applicationContext)
                    .logEvent(event.eventName(), event.params());
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