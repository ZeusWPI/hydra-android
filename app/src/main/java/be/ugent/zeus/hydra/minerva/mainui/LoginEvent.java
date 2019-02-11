package be.ugent.zeus.hydra.minerva.mainui;

import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.Event;

/**
 * @author Niko Strijbol
 */
public class LoginEvent implements Event {
    @Nullable
    @Override
    public String getEventName() {
        return Reporting.getEvents().login();
    }
}
