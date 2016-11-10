package be.ugent.zeus.hydra.plugins;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.plugins.common.Plugin;

/**
 * Plugin to send default analytics screen.
 *
 * @author Niko Strijbol
 */
public class AnalyticsPlugin extends Plugin {

    private NameProvider screenName;

    public AnalyticsPlugin(NameProvider screenName) {
        this.screenName = screenName;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendScreen((HydraApplication) getHost().getContext().getApplicationContext());
    }

    private void sendScreen(HydraApplication application) {
        application.sendScreenName(screenName.provideName());
    }

    @FunctionalInterface
    public interface NameProvider {
        String provideName();
    }
}