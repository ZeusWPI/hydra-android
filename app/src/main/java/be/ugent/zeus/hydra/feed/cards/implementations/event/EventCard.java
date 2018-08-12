package be.ugent.zeus.hydra.feed.cards.implementations.event;

import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import java9.util.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

/**
 * Home card for {@link Event}.
 *
 * @author silox
 * @author Niko Strijbol
 */
class EventCard extends Card {

    private Event event;

    EventCard(Event event) {
        this.event = event;
    }

    @Override
    public int getPriority() {
        Duration duration = Duration.between(ZonedDateTime.now(), event.getStart());
        //Add some to 24*30 for better ordering
        return PriorityUtils.lerp((int) duration.toHours(), 0, 744);
    }

    @Override
    public String getIdentifier() {
        return event.getIdentifier();
    }

    @Override
    public int getCardType() {
        return Card.Type.ACTIVITY;
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