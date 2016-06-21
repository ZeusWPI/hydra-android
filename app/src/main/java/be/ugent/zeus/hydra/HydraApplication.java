package be.ugent.zeus.hydra;

import android.app.Application;
import android.content.res.Configuration;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Locale;

import be.ugent.android.sdk.UGentApplication;
import be.ugent.android.sdk.oauth.OAuthConfiguration;

/**
 * Created by feliciaan on 06/04/16.
 */
public class HydraApplication extends UGentApplication {
    private Tracker tracker;

    public static final Locale LOCALE = new Locale("nl");

    @Override
    public void onCreate()
    {
        super.onCreate();

        JodaTimeAndroid.init(this);

        Locale.setDefault(LOCALE);
        Configuration config = new Configuration();
        config.locale = LOCALE;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public OAuthConfiguration getOAuthConfiguration() {
        return new OAuthConfiguration.Builder()
                .apiKey(BuildConfig.OAUTH_ID)
                .apiSecret(BuildConfig.OAUTH_SECRET)
                .callbackUri("https://zeus.ugent.be/hydra/oauth/callback")
                .build();
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
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
