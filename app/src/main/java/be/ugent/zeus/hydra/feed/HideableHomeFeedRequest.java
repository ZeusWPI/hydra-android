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

package be.ugent.zeus.hydra.feed;

import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.CardIdentifier;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;

/**
 * Home feed request that takes care of maintaining and hiding cards the user no longer wants to see.
 *
 * @author Niko Strijbol
 */
public abstract class HideableHomeFeedRequest implements HomeFeedRequest {

    private final DismissalDao dismissalDao;

    protected HideableHomeFeedRequest(DismissalDao dismissalDao) {
        this.dismissalDao = dismissalDao;
    }

    @NonNull
    @Override
    public final Result<Stream<Card>> execute(@NonNull Bundle args) {
        return performRequestCards(args).map(cardsStream -> {
            List<Card> cards = cardsStream.collect(Collectors.toList());
            // Remove all stale hidden cards.
            dismissalDao.prune(getCardType(), cards);

            // Hide cards that we don't want to show anymore.
            List<CardIdentifier> hiddenList = dismissalDao.getIdsForType(getCardType());
            // If hidden is empty, we don't do anything for performance reasons.
            if (hiddenList.isEmpty()) {
                return cards.stream();
            } else {
                // Wrap in a set for fast contains.
                Collection<CardIdentifier> fastHidden = new HashSet<>(hiddenList);
                return cards.stream()
                        .filter(card -> !fastHidden.contains(new CardIdentifier(card.getCardType(), card.getIdentifier())));
            }
        });
    }

    @NonNull
    protected abstract Result<Stream<Card>> performRequestCards(@NonNull Bundle args);
}
