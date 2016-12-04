package be.ugent.zeus.hydra.homefeed.content.specialevent;

import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import java8.util.Objects;

/**
 * Home card for a {@link SpecialEvent}.
 *
 * @author Niko Strijbol
 * @author silox
 */
class SpecialEventCard extends HomeCard {

    private final SpecialEvent specialEvent;

    SpecialEventCard(SpecialEvent specialEvent) {
        this.specialEvent = specialEvent;
    }

    SpecialEvent getSpecialEvent() {
        return specialEvent;
    }

    @Override
    public int getPriority() {
        return specialEvent.getPriority();
    }

    @Override
    public int getCardType() {
        return CardType.SPECIAL_EVENT;
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