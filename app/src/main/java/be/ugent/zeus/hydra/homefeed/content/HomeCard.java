package be.ugent.zeus.hydra.homefeed.content;

import android.support.annotation.IntDef;
import java8.lang.Integers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.ugent.zeus.hydra.homefeed.content.HomeCard.CardType.*;

/**
 * Base model for the cards in the home feed.
 *
 * Every subclass should have a {@link CardType} associated with it. This is to facilitate working with adapters.
 *
 * Every card must give itself a priority in [0,1000]. This defines the natural ordening of the cards; 0 is the
 * card with the highest priority, 1000 has the lowest priority. Cards should generally strive to produce unique
 * priorities for a certain card type, as the order of two cards with the same priority is not defined.
 *
 * An easy way to calculate a correct priority is using {@link FeedUtils}, which can calculate a priority for a
 * card that has a score in an interval, e.g. the days between the card's date and today.
 *
 * Implementations must provide working {@link #equals(Object)} and {@link #hashCode()} functions. These functions are
 * used to calculate differences between collections of cards.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public abstract class HomeCard implements Comparable<HomeCard> {

    /**
     * @return Priority should be a number between 0 and 1000. See the class description.
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
    @IntDef({RESTO, ACTIVITY, SPECIAL_EVENT, SCHAMPER, NEWS_ITEM, MINERVA_LOGIN, MINERVA_ANNOUNCEMENT, MINERVA_AGENDA, DEBUG})
    public @interface CardType {
        int RESTO = 1;
        int ACTIVITY = 2;
        int SPECIAL_EVENT = 3;
        int SCHAMPER = 4;
        int NEWS_ITEM = 5;
        int MINERVA_LOGIN = 6;
        int MINERVA_ANNOUNCEMENT = 7;
        int MINERVA_AGENDA = 8;
        int DEBUG = 100;
    }

    /**
     * Check the card type of this card, and return a casted version.
     *
     * This method is necessary due to the shortcomings of Java's type system.
     *
     * @param type The type you need.
     * @param <C> The type of card you need.
     * @return The cast card if it is of the right type.
     */
    public <C extends HomeCard> C checkCard(@CardType int type) {
        if (getCardType() != type) {
            throw new ClassCastException("This card has the wrong type.");
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
        return Integers.compare(this.getPriority(), homeCard.getPriority());
    }
}