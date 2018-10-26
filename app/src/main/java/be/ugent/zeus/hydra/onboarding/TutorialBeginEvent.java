package be.ugent.zeus.hydra.onboarding;

import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.common.analytics.Analytics;
import be.ugent.zeus.hydra.common.analytics.Event;

/**
 * Event to log when a user starts the onboarding.
 *
 * @author Niko Strijbol
 */
final class TutorialBeginEvent implements Event {

    @Nullable
    @Override
    public String getEventName() {
        return Analytics.getEvents().tutorialBegin();
    }
}
