package be.ugent.zeus.hydra.ui.main.homefeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.feed.CardIdentifier;
import be.ugent.zeus.hydra.feed.CardRepository;
import be.ugent.zeus.hydra.repository.requests.Result;
import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public final Result<Stream<Card>> performRequest(@Nullable Bundle args) {
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
                Set<CardIdentifier> fastHidden = new HashSet<>(hiddenList);
                return StreamSupport.stream(cards)
                        .filter(card -> !fastHidden.contains(new CardIdentifier(card.getCardType(), card.getIdentifier())));
            }
        });
    }

    @NonNull
    protected abstract Result<Stream<Card>> performRequestCards(@Nullable Bundle args);
}
