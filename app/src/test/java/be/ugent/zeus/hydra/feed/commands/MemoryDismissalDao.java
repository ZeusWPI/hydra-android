package be.ugent.zeus.hydra.feed.commands;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.dismissal.CardIdentifier;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MemoryDismissalDao extends DismissalDao {

    private final Set<CardDismissal> dismissals = new HashSet<>();

    @Override
    public List<CardDismissal> getForType(int cardType) {
        return dismissals.stream().filter(d -> d.getIdentifier().getCardType() == cardType).collect(Collectors.toList());
    }

    @Override
    public List<CardIdentifier> getIdsForType(int type) {
        return dismissals.stream()
                .map(CardDismissal::getIdentifier)
                .filter(i -> i.getCardType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public void insert(CardDismissal dismissal) {
        dismissals.add(dismissal);
    }

    @Override
    public void update(CardDismissal cardDismissal) {
        dismissals.add(cardDismissal);
    }

    @Override
    public void prune(int cardType, List<Card> allCards) {
        List<CardDismissal> typeDismissals = getForType(cardType);
        Set<CardIdentifier> cardSet = allCards.stream().map(c -> new CardIdentifier(c.getCardType(), c.getIdentifier())).collect(Collectors.toSet());
        for (CardDismissal dismissal : typeDismissals) {
            if (!cardSet.contains(dismissal.getIdentifier())) {
                delete(dismissal);
            }
        }
    }

    @Override
    public void delete(CardDismissal dismissal) {
        dismissals.remove(dismissal);
    }

    @Override
    public void deleteAll() {
        dismissals.clear();
    }

    @Override
    protected void deleteCard(int cardType, String id) {
        dismissals.removeIf(cardDismissal -> {
            CardIdentifier identifier = cardDismissal.getIdentifier();
            return identifier.getCardType() == cardType && identifier.getIdentifier().equals(id);
        });
    }
}
