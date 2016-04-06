package be.ugent.zeus.hydra;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by feliciaan on 06/04/16.
 */
public class HydraApplication extends Application {
    private Tracker tracker;


    @Override
    public void onCreate()
    {
        super.onCreate();

        JodaTimeAndroid.init(this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }
}
