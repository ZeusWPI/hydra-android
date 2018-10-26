package be.ugent.zeus.hydra.onboarding;

import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.common.analytics.Analytics;
import be.ugent.zeus.hydra.common.analytics.Event;

/**
 * @author Niko Strijbol
 */
class TutorialEndEvent implements Event {
    @Nullable
    @Override
    public String getEventName() {
        return Analytics.getEvents().tutorialComplete();
    }
}
