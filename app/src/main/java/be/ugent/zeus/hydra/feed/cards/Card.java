package be.ugent.zeus.hydra.feed.cards;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java9.lang.Integers;

import static be.ugent.zeus.hydra.feed.cards.Card.Type.*;

/**
 * Every subclass should have a {@link Card.Type} associated with it. This is to facilitate working with adapters.
 *
 * <h1>Priority</h1>
 * Every card must give itself a priority in [0,1000]. This defines the natural ordening of the cards; 0 is the
 * card with the highest priority, 1000 has the lowest priority. Cards should generally strive to produce unique
 * priorities for a certain card type, as the order of two cards with the same priority is not defined.
 *
 * An easy way to calculate a correct priority is using {@link PriorityUtils}, which can calculate a priority for a
 * card that has a score in an interval, e.g. the days between the card's date and today.
 *
 * The implementation shifts the priority to [10,1010]. The first interval [0,10[ should be used very sparingly for
 * special occasions, such as giving the resto card a temporarily higher score because it is eating time.
 *
 * The negative values ]-Inf,0[ are reserved for use with special cards.
 *
 * <h1>Identifier</h1>
 * Each card instance should have an unique identifier. The identifier must be unique within the card type.
 *
 * The identifier is used to identify card instances that are conceptually the same card. Note that the content does
 * not need to be identical. An example is a calendar card that shows the events for a certain day. Although the content
 * might change during the day or even before the day, it represents the same card nonetheless. In other words, this
 * depends on the functional requirements of the application.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
@SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
public abstract class Card implements Comparable<Card> {

    /**
     * @return The card type.
     */
    @Type
    public abstract int getCardType();

    /**
     * @return Priority should be a number between 0 and 1010. See the class description.
     */
    public abstract int getPriority();

    /**
     * Get the unique (under the card type) identifier.
     *
     * @return Unique identifier for this card under the card type.
     */
    public abstract String getIdentifier();

    /**
     * Note: the numbers are not sequential due to removed types.
     * DO NOT re-use numbers: these are saved in the database.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RESTO, ACTIVITY, SPECIAL_EVENT, SCHAMPER, NEWS_ITEM, URGENT_FM, LIBRARY, DEBUG})
    public @interface Type {
        int RESTO = 1;
        int ACTIVITY = 2;
        int SPECIAL_EVENT = 3;
        int SCHAMPER = 4;
        int NEWS_ITEM = 5;
        int URGENT_FM = 9;
        int LIBRARY = 10;
        int DEBUG = 100;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    /**
     * The ordering of cards is implemented using the {@link #getPriority()} function.
     * As long as that function is correctly implemented, this class guarantees a correct
     * implementation of the comparison.
     *
     * The cards are ordered in an ascending manner; more information is available in the class description.
     *
     * Note: this class has a natural ordering that is inconsistent with equals.
     */
    @Override
    public int compareTo(@NonNull Card card) {
        return Integers.compare(this.getPriority(), card.getPriority());
    }

    /**
     * Check the card type of this card, and return a casted version.
     *
     * This method is necessary due to the shortcomings of Java's type system.
     *
     * @param type The type you need.
     * @param <C>  The type of card you need.
     *
     * @return The cast card if it is of the right type.
     */
    public <C extends Card> C checkCard(@Type int type) {
        if (getCardType() != type) {
            throw new ClassCastException("This card has the wrong type.");
        }

        //noinspection unchecked
        return (C) this;
    }
}
