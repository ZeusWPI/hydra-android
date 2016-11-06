package be.ugent.zeus.hydra.activities.plugins;

import android.util.Log;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.activities.plugins.common.Plugin;

/**
 * Plugin to send default analytics screen.
 *
 * @author Niko Strijbol
 */
public class AnalyticsPlugin extends Plugin {

    private static final String TAG = "AnalyticsPlugin";

    private NameProvider screenName;

    public AnalyticsPlugin() {
        this(null);
    }

    public AnalyticsPlugin(NameProvider screenName) {
        this.screenName = screenName;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        sendScreen((HydraApplication) getHost().getContext().getApplicationContext());
    }

    private void sendScreen(HydraApplication application) {
        Log.d(TAG, "Sent message.");
        if(screenName == null) {
            application.sendScreenName(getHost().getHostClass().getSimpleName());
        } else {
            application.sendScreenName(screenName.provideName());
        }
    }

    @FunctionalInterface
    public interface NameProvider {
        String provideName();
    }
}