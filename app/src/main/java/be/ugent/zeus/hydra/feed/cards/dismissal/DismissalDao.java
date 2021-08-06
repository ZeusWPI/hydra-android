/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.feed.cards.dismissal;

import androidx.room.*;

import java.util.*;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * @author Niko Strijbol
 */
@Dao
public abstract class DismissalDao {

    /**
     * Get all dismissals for a certain card type.
     *
     * @param type The type of cards to get dismissals for.
     * @return The dismissals.
     */
    @Query("SELECT * FROM " + DismissalTable.TABLE_NAME + " WHERE " + DismissalTable.Columns.CARD_TYPE + " = :type")
    public abstract List<CardDismissal> getForType(@Card.Type int type);

    /**
     * Get all ids of the dismissals for a certain card type.
     *
     * @param type The type of cards to get dismissals for.
     * @return The dismissals.
     */
    @Query("SELECT " + DismissalTable.Columns.CARD_TYPE + ", " + DismissalTable.Columns.IDENTIFIER + " FROM " + DismissalTable.TABLE_NAME + " WHERE " + DismissalTable.Columns.CARD_TYPE + " = :type")
    public abstract List<CardIdentifier> getIdsForType(@Card.Type int type);

    @Insert
    public abstract void insert(CardDismissal dismissal);

    @Update
    public abstract void update(CardDismissal dismissal);

    @Delete
    public abstract void delete(CardDismissal dismissal);

    /**
     * Delete all cards.
     */
    @Query("DELETE FROM " + DismissalTable.TABLE_NAME)
    public abstract void deleteAll();

    /**
     * Delete cards from the database based on their id.
     * <p>
     * Note: you probably want to use {@link #deleteByIdentifier(Collection)} instead.
     *
     * @param cardType The type of the card.
     * @param id       The identifier of the card.
     */
    @Query("DELETE FROM " + DismissalTable.TABLE_NAME + " WHERE " + DismissalTable.Columns.CARD_TYPE + " = :cardType AND " + DismissalTable.Columns.IDENTIFIER + " = :id")
    protected abstract void deleteCard(@Card.Type int cardType, String id);

    @Transaction
    public void deleteByIdentifier(Collection<CardIdentifier> identifiers) {
        for (CardIdentifier identifier : identifiers) {
            deleteCard(identifier.getCardType(), identifier.getIdentifier());
        }
    }

    /**
     * Remove all saved dismissals for cards that are not in the given list of cards.
     *
     * @param type     The card type of the given cards.
     * @param allCards All cards. Cards not present in this list will be removed from the repository.
     */
    public void prune(@Card.Type int type, List<Card> allCards) {
        Set<CardIdentifier> dismissals = new HashSet<>(getIdsForType(type));

        Set<CardIdentifier> retained = allCards.stream()
                .map(c -> new CardIdentifier(c.getCardType(), c.getIdentifier()))
                .collect(Collectors.toSet());

        // Retain only the ones that are no longer in the list of all cards.
        dismissals.removeAll(retained);

        // Delete the others.
        deleteByIdentifier(dismissals);
    }
}
