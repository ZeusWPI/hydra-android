package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.utils.Objects;

/**
 * Home card for a {@link SpecialEvent}.
 *
 * @author Niko Strijbol
 * @author silox
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialEventCard that = (SpecialEventCard) o;
        return Objects.equals(specialEvent, that.specialEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specialEvent);
    }
}