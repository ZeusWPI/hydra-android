package be.ugent.zeus.hydra.feed;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public interface CardRepository {

    /**
     * Get all dismissals for a certain card type.
     *
     * @param cardType The type of cards to get dismissals for.
     *
     * @return The dismissals.
     */
    List<CardDismissal> getForType(@Card.Type int cardType);

    /**
     * Get all ids of the dismissals for a certain card type.
     *
     * @param cardType The type of cards to get dismissals for.
     *
     * @return The dismissals.
     */
    List<CardIdentifier> getIdForType(@Card.Type int cardType);

    /**
     * Add a new dismissal.
     *
     * @param cardDismissal The dismissal.
     */
    void add(CardDismissal cardDismissal);

    /**
     * Update an existing dismissal.
     *
     * @param cardDismissal This dismissal.
     */
    void update(CardDismissal cardDismissal);

    /**
     * Remove all saved dismissals for cards that are not in the given list of cards.
     *
     * @param cardType The card type of the given cards.
     * @param allCards All cards. Cards not present in this list will be removed from the repository.
     */
    void prune(@Card.Type int cardType, final List<Card> allCards);

    /**
     * Delete a card dismissal if it is present.
     *
     * @param dismissal The dismissal to remove.
     */
    void delete(CardDismissal dismissal);

    /**
     * Delete all cards.
     */
    void deleteAll();
}