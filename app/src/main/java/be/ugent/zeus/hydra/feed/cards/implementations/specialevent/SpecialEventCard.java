package be.ugent.zeus.hydra.feed.cards.implementations.specialevent;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.specialevent.SpecialEvent;
import java8.util.Objects;

/**
 * Home card for a {@link SpecialEvent}.
 *
 * @author Niko Strijbol
 * @author silox
 */
class SpecialEventCard extends Card {

    private final SpecialEvent specialEvent;

    SpecialEventCard(SpecialEvent specialEvent) {
        this.specialEvent = specialEvent;
    }

    SpecialEvent getSpecialEvent() {
        return specialEvent;
    }

    @Override
    public int getPriority() {
        //We get the complement, as the server assumes 1000 = highest priority. This is for
        //historical reasons.
        return PriorityUtils.FEED_MAX_VALUE - specialEvent.getPriority() - 2 * PriorityUtils.FEED_SPECIAL_SHIFT;
    }

    @Override
    public String getIdentifier() {
        return String.valueOf(specialEvent.getId());
    }

    @Override
    public int getCardType() {
        return Card.Type.SPECIAL_EVENT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialEventCard that = (SpecialEventCard) o;
        return Objects.equals(specialEvent, that.specialEvent);
    }

    @Override
    public int hashCode() {
        return java8.util.Objects.hash(specialEvent);
    }
}