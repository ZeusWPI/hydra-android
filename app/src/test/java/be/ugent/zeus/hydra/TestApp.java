package be.ugent.zeus.hydra;

import android.app.Activity;
import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.common.ChannelCreator;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import jonathanfinerty.once.Once;

/**
 * Does not use the ThreeTenABP because of a bug in Robolectric.
 *
 * @author Niko Strijbol
 */
public class TestApp extends HydraApplication {

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }

        if (BuildConfig.DEBUG) {
            enableStrictModeInDebug();
        }

        // This below is disabled for tests.
        //AndroidThreeTen.init(this);
        //LeakCanary.install(this);
        Once.initialise(this);

        // Initialize the channels that are needed in the whole app. The channels for Minerva are created when needed.
        createChannels();
    }

    @Override
    protected void onCreateInitialise() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }

        if (BuildConfig.DEBUG) {
            enableStrictModeInDebug();
        }

        // This below is disabled for tests.
        //AndroidThreeTen.init(this);
        //LeakCanary.install(this);
        Once.initialise(this);

        // Initialize the channels that are needed in the whole app. The channels for Minerva are created when needed.
        createChannels();
    }
}
