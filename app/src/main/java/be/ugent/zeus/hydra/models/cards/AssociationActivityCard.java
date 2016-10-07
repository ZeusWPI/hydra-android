package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.association.Event;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

/**
 * Created by silox on 18/04/16.
 */
public class AssociationActivityCard extends HomeCard {

    private Event event;

    public AssociationActivityCard(Event event) {
        this.event = event;
    }

    @Override
    public int getPriority() {
        Duration duration = Duration.between(ZonedDateTime.now(), event.getStart());
        return 950 - Math.max(0, (int) duration.toHours()) * 4; //see 10 days in to the future
    }

    @Override
    public int getCardType() {
        return CardType.ACTIVITY;
    }

    public Event getEvent() {
        return event;
    }
}