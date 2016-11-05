package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.utils.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;


/**
 * Home card for {@link Event}.
 *
 * @author silox
 * @author Niko Strijbol
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventCard eventCard = (EventCard) o;
        return Objects.equals(event, eventCard.event);
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }
}