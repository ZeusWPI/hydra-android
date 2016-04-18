package be.ugent.zeus.hydra.models.cards;

import android.widget.GridLayout;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;

/**
 * Created by silox on 18/04/16.
 */
public class SpecialEventCard extends HomeCard {

    private SpecialEvent specialEvent;

    public SpecialEventCard(SpecialEvent specialEvent) {
        this.specialEvent = specialEvent;
    }

    @Override
    public int getPriority() {
        return getSpecialEvent().getPriority();
    }

    @Override
    public HomeCardAdapter.HomeType getCardType() {
        return HomeCardAdapter.HomeType.SPECIALEVENT;
    }

    public SpecialEvent getSpecialEvent() {
        return specialEvent;
    }
}
