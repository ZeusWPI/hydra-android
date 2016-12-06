package be.ugent.zeus.hydra.homefeed.content.minerva.login;

import be.ugent.zeus.hydra.homefeed.content.HomeCard;

/**
 * Home card to prompt the user to log in.
 *
 * @author Niko Strijbol
 * @author felicaan
 */
public class MinervaLoginCard extends HomeCard {

    @Override
    public int getPriority() {
        return 50;
    }

    @Override
    public int getCardType() {
        return CardType.MINERVA_LOGIN;
    }

    @Override
    public int hashCode() {
        return java8.util.Objects.hashCode(getClass().getCanonicalName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}