package be.ugent.zeus.hydra.minerva.mainui;

import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.common.analytics.Analytics;
import be.ugent.zeus.hydra.common.analytics.Event;

/**
 * @author Niko Strijbol
 */
public class LoginEvent implements Event {
    @Nullable
    @Override
    public String getEventName() {
        return Analytics.getEvents().login();
    }
}
