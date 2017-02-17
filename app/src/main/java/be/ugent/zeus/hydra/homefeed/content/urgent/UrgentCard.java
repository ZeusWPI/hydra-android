package be.ugent.zeus.hydra.homefeed.content.urgent;

import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import java8.util.Objects;

/**
 * @author Niko Strijbol
 */
public class UrgentCard extends HomeCard {

    @Override
    public int getPriority() {
        return -5;
    }

    @Override
    public int getCardType() {
        return CardType.URGENT_FM;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof UrgentCard;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCardType());
    }
}