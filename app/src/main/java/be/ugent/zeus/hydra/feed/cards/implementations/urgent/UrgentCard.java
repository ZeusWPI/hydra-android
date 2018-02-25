package be.ugent.zeus.hydra.feed.cards.implementations.urgent;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;

/**
 * @author Niko Strijbol
 */
public class UrgentCard extends Card {

    private static final String TAG = "UrgentCard";

    @Override
    public int getPriority() {
        return PriorityUtils.FEED_SPECIAL_SHIFT + 1;
    }

    @Override
    public String getIdentifier() {
        return TAG;
    }

    @Override
    public int getCardType() {
        return Card.Type.URGENT_FM;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UrgentCard;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}