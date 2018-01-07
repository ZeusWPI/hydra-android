package be.ugent.zeus.hydra.ui.main.homefeed.commands;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.domain.models.feed.CardDismissal;
import be.ugent.zeus.hydra.domain.models.feed.CardIdentifier;
import be.ugent.zeus.hydra.domain.repository.CardRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MemoryCardRepository implements CardRepository {

    private Set<CardDismissal> dismissals = new HashSet<>();

    @Override
    public List<CardDismissal> getForType(int cardType) {
        return dismissals.stream().filter(d -> d.getIdentifier().getCardType() == cardType).collect(Collectors.toList());
    }

    @Override
    public List<CardIdentifier> getIdForType(int cardType) {
        return dismissals.stream()
                .map(CardDismissal::getIdentifier)
                .filter(i -> i.getCardType() == cardType)
                .collect(Collectors.toList());
    }

    @Override
    public void add(CardDismissal cardDismissal) {
        dismissals.add(cardDismissal);
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
}
