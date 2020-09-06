package be.ugent.zeus.hydra.feed.cards.event;

import android.util.Pair;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.AssociationMap;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;

/**
 * Home card for {@link Event}.
 *
 * @author silox
 * @author Niko Strijbol
 */
class EventCard extends Card {

    private final Event event;
    private final AssociationMap map;

    EventCard(Event event, AssociationMap associationMap) {
        this.event = event;
        this.map = associationMap;
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

    public Pair<Event, Association> getEvent() {
        return new Pair<>(event, map.get(event.getAssociation()));
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
