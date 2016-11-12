package be.ugent.zeus.hydra.fragments.home.operations;

import be.ugent.zeus.hydra.models.cards.HomeCard;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public final class FeedUtils {

    private FeedUtils() {}

    /**
     * Remove a card type from a given list. This will operate in place.
     *
     * @param data The list to remove the card from.
     * @param cardType The type of card to remove.
     */
    public static void remove(List<HomeCard> data, @HomeCard.CardType int cardType) {
        //Remove all cards from this type
        Iterator<HomeCard> it = data.iterator();
        while (it.hasNext()) { // Why no filter :(
            HomeCard c = it.next();
            if (c.getCardType() == cardType) {
                it.remove();
            }
        }
    }

    /**
     * Add cards to a list and sort the result.
     *
     * @param list The list to add cards to. This list will be modified.
     * @param toAdd The cards to add.
     * @param type The type of card.
     */
    public static void addAndSort(List<HomeCard> list, Collection<HomeCard> toAdd, @HomeCard.CardType int type) {
        //Remove all cards from this type
        remove(list, type);
        list.addAll(toAdd);
        Collections.sort(list);
    }
}