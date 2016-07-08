package be.ugent.zeus.hydra;

import android.app.Application;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import net.danlew.android.joda.JodaTimeAndroid;


/**
 * Created by feliciaan on 06/04/16.
 */
public class HydraApplication extends Application {

    private Tracker tracker;
    private AuthorizationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
    }

    public AuthorizationManager getAuthorizationManager() {
        if(manager == null) {
            manager = new AuthorizationManager(getApplicationContext());
        }

        return manager;
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            if (BuildConfig.DEBUG) {
                // disable google analytics while debugging
                GoogleAnalytics.getInstance(this).setDryRun(true);
            }

            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }

    public void sendScreenName(String screenName) {
        Tracker t = getDefaultTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
