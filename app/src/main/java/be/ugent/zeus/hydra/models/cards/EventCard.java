package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.association.Event;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

/**
 * Show association event.
 *
 * @author silox
 */
public class EventCard extends HomeCard {

    private Event event;

    public EventCard(Event event) {
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