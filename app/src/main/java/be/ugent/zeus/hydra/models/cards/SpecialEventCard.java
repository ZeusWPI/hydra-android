package be.ugent.zeus.hydra.models.cards;

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
    public int getCardType() {
        return CardType.SPECIAL_EVENT;
    }

    public SpecialEvent getSpecialEvent() {
        return specialEvent;
    }
}
