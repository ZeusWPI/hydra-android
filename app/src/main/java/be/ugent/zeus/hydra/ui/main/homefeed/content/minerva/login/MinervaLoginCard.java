package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.login;

import be.ugent.zeus.hydra.feed.Card;

/**
 * Home card to prompt the user to log in.
 *
 * @author Niko Strijbol
 * @author felicaan
 */
public class MinervaLoginCard extends Card {

    private static final String TAG = "MinervaLoginCard";

    @Override
    public int getPriority() {
        return 50;
    }

    @Override
    public String getIdentifier() {
        return TAG;
    }

    @Override
    public int getCardType() {
        return Card.Type.MINERVA_LOGIN;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MinervaLoginCard;
    }
}