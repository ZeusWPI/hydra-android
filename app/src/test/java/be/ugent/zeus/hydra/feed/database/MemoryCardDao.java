package be.ugent.zeus.hydra.feed.database;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.feed.CardDismissal;
import be.ugent.zeus.hydra.feed.CardIdentifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MemoryCardDao extends CardDao {

    private Map<CardIdentifier, CardDismissal> dismissalMap = new HashMap<>();

    public MemoryCardDao(Collection<CardDismissal> initialData) {
        super(null);
        for (CardDismissal dismissal: initialData) {
            dismissalMap.put(dismissal.getIdentifier(), dismissal);
        }
    }

    @Override
    public List<CardDismissal> getForType(int type) {
        return dismissalMap.values().stream()
                .filter(d -> d.getIdentifier().getCardType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<CardIdentifier> getIdsForType(int type) {
        return dismissalMap.values().stream()
                .map(CardDismissal::getIdentifier)
                .filter(i -> i.getCardType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public void insert(CardDismissal dismissal) {
        dismissalMap.put(dismissal.getIdentifier(), dismissal);
    }

    @Override
    public void update(CardDismissal cardDismissal) {
        dismissalMap.put(cardDismissal.getIdentifier(), cardDismissal);
    }

    @Override
    public void delete(CardDismissal cardDismissal) {
        dismissalMap.remove(cardDismissal.getIdentifier());
    }

    @Override
    public void deleteAll() {
        dismissalMap.clear();
    }

    @Override
    public void deleteByIdentifier(Collection<CardIdentifier> identifiers) {
        dismissalMap.entrySet().removeIf(e -> identifiers.contains(e.getKey()));
    }
}