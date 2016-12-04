package be.ugent.zeus.hydra.homefeed.content.event;

import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.association.Event;
import java8.util.Objects;
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
        long calc = 1000 - (duration.toHours() * 6) + (event.getTitle().charAt(0) + event.getAssociation().getInternalName().charAt(0)) / 2;
        return Math.min((int) Math.max(0, calc), 1000);
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