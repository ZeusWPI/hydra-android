package be.ugent.zeus.hydra.domain.repository;

import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.domain.models.feed.CardDismissal;

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
    void prune(@Card.Type int cardType, List<Card> allCards);
}