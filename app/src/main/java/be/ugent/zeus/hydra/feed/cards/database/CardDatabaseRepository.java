package be.ugent.zeus.hydra.feed.cards.database;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.CardIdentifier;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class CardDatabaseRepository implements CardRepository {

    private final CardDao cardDao;

    public CardDatabaseRepository(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    @Override
    public List<CardDismissal> getForType(int cardType) {
        return cardDao.getForType(cardType);
    }

    @Override
    public List<CardIdentifier> getIdForType(@Card.Type int cardType) {
        return cardDao.getIdsForType(cardType);
    }

    @Override
    public void add(CardDismissal cardDismissal) {
        cardDao.insert(cardDismissal);
    }

    @Override
    public void update(CardDismissal cardDismissal) {
        cardDao.update(cardDismissal);
    }

    @Override
    public void prune(@Card.Type int cardType, List<Card> allCards) {
        Set<CardIdentifier> dismissals = new HashSet<>(getIdForType(cardType));

        Set<CardIdentifier> retained = StreamSupport.stream(allCards)
                .map(c -> new CardIdentifier(c.getCardType(), c.getIdentifier()))
                .collect(Collectors.toSet());

        // Retain only the ones that are no longer in the list of all cards.
        dismissals.removeAll(retained);

        // Delete the others.
        cardDao.deleteByIdentifier(dismissals);
    }

    @Override
    public void delete(CardDismissal dismissal) {
        cardDao.delete(dismissal);
    }

    @Override
    public void deleteAll() {
        cardDao.deleteAll();
    }
}
