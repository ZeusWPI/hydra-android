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
        return dismissals.stream().filter(d -> d.identifier().getCardType() == cardType).collect(Collectors.toList());
    }

    @Override
    public List<CardIdentifier> getIdsForType(int type) {
        return dismissals.stream()
                .map(CardDismissal::identifier)
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
        Set<CardIdentifier> cardSet = allCards.stream().map(c -> new CardIdentifier(c.cardType(), c.identifier())).collect(Collectors.toSet());
        for (CardDismissal dismissal : typeDismissals) {
            if (!cardSet.contains(dismissal.identifier())) {
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
            CardIdentifier identifier = cardDismissal.identifier();
            return identifier.getCardType() == cardType && identifier.getIdentifier().equals(id);
        });
    }
}
