package be.ugent.zeus.hydra.feed.cards.database;

import androidx.room.*;

import java.util.Collection;
import java.util.List;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.CardIdentifier;

/**
 * @author Niko Strijbol
 */
@Dao
public abstract class CardDao {

    @Query("SELECT * FROM " + DismissalTable.TABLE_NAME + " WHERE " + DismissalTable.Columns.CARD_TYPE + " = :type")
    public abstract List<CardDismissal> getForType(@Card.Type int type);

    @Query("SELECT " + DismissalTable.Columns.CARD_TYPE + ", " + DismissalTable.Columns.IDENTIFIER + " FROM " + DismissalTable.TABLE_NAME + " WHERE " + DismissalTable.Columns.CARD_TYPE + " = :type")
    public abstract List<CardIdentifier> getIdsForType(@Card.Type int type);

    @Insert
    public abstract void insert(CardDismissal dismissal);

    @Update
    public abstract void update(CardDismissal cardDismissal);

    @Delete
    public abstract void delete(CardDismissal cardDismissal);

    @Query("DELETE FROM " + DismissalTable.TABLE_NAME)
    public abstract void deleteAll();

    /**
     * Delete cards from the database based on their id.
     *
     * Note: you probably want to use {@link #deleteByIdentifier(Collection)} instead.
     *
     * @param cardType The type of the card.
     * @param id The identifier of the card.
     */
    @Query("DELETE FROM " + DismissalTable.TABLE_NAME + " WHERE " + DismissalTable.Columns.CARD_TYPE + " = :cardType AND " + DismissalTable.Columns.IDENTIFIER + " = :id")
    protected abstract void deleteCard(@Card.Type int cardType, String id);

    @Transaction
    public void deleteByIdentifier(Collection<CardIdentifier> identifiers) {
        for (CardIdentifier identifier: identifiers) {
            deleteCard(identifier.getCardType(), identifier.getIdentifier());
        }
    }
}