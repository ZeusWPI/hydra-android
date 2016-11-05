package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.utils.Objects;

/**
 * Home card to prompt the user to log in.
 *
 * @author Niko Strijbol
 * @author felicaan
 */
public class MinervaLoginCard extends HomeCard {

    @Override
    public int getPriority() {
        return 850;
    }

    @Override
    public int getCardType() {
        return CardType.MINERVA_LOGIN;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass().getCanonicalName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}