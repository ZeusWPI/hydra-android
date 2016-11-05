package be.ugent.zeus.hydra.models.cards;

import android.os.Build;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.*;

/**
 * Base model for the cards in the home feed.
 *
 * Every subclass should have a {@link CardType} associated with it. This is to facilitate working with adapters.
 *
 * Every card must define a priority between 0 and 1000. This value is also used as the base for the comparison.
 *
 * Implementations must provide working {@link #equals(Object)} and {@link #hashCode()} functions.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public abstract class HomeCard implements Comparable<HomeCard> {

    /**
     * @return Priority should be a number between min inf and 1000.
     */
    public abstract int getPriority();

    /**
     * @return The card type.
     */
    @HomeCard.CardType
    public abstract int getCardType();

    /**
     * Android is horrible with enums, since Google doesn't know what they are doing apparently. Sigh.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RESTO, ACTIVITY, SPECIAL_EVENT, SCHAMPER, NEWS_ITEM, MINERVA_LOGIN, MINERVA_ANNOUNCEMENT, MINERVA_AGENDA})
    public @interface CardType {
        int RESTO = 1;
        int ACTIVITY = 2;
        int SPECIAL_EVENT = 3;
        int SCHAMPER = 4;
        int NEWS_ITEM = 5;
        int MINERVA_LOGIN = 6;
        int MINERVA_ANNOUNCEMENT = 7;
        int MINERVA_AGENDA = 8;
    }

    /**
     * Check the card type of this card.
     *
     * @param type The type you need.
     * @param <C> The type of card you need.
     * @return The cast card if it is of the right type.
     */
    public <C extends HomeCard> C checkCard(@CardType int type) {
        if(getCardType() != type) {
            throw new IllegalStateException("This card type is wrong.");
        }

        //noinspection unchecked
        return (C) this;
    }

    /**
     * This method is abstract to force an implementation.
     *
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode();

    /**
     * This method is abstract to force an implementation.
     *
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * Items are compared using {@link #getPriority()}.
     *
     * {@inheritDoc}
     */
    @Override
    public int compareTo(HomeCard homeCard) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return -Integer.compare(this.getPriority(), homeCard.getPriority());
        } else {
            int x = this.getPriority();
            int y = homeCard.getPriority();
            return -((x < y) ? -1 : ((x == y) ? 0 : 1));
        }
    }
}