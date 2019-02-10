package be.ugent.zeus.hydra.feed;

import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardIdentifier;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import java9.util.stream.Collectors;
import java9.util.stream.Stream;
import java9.util.stream.StreamSupport;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Home feed request that takes care of maintaining and hiding cards the user no longer wants to see.
 * @author Niko Strijbol
 */
public abstract class HideableHomeFeedRequest implements HomeFeedRequest {

    private final CardRepository cardRepository;

    protected HideableHomeFeedRequest(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @NonNull
    @Override
    public final Result<Stream<Card>> execute(@NonNull Bundle args) {
        return performRequestCards(args).map(cardsStream -> {
            List<Card> cards = cardsStream.collect(Collectors.toList());
            // Remove all stale hidden cards.
            cardRepository.prune(getCardType(), cards);

            // Hide cards that we don't want to show anymore.
            List<CardIdentifier> hiddenList = cardRepository.getIdForType(getCardType());
            // If hidden is empty, we don't do anything for performance reasons.
            if (hiddenList.isEmpty()) {
                return StreamSupport.stream(cards);
            } else {
                // Wrap in a set for fast contains.
                Collection<CardIdentifier> fastHidden = new HashSet<>(hiddenList);
                return StreamSupport.stream(cards)
                        .filter(card -> !fastHidden.contains(new CardIdentifier(card.getCardType(), card.getIdentifier())));
            }
        });
    }

    @NonNull
    protected abstract Result<Stream<Card>> performRequestCards(@NonNull Bundle args);
}
