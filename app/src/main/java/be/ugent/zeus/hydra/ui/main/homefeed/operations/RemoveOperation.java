package be.ugent.zeus.hydra.ui.main.homefeed.operations;

import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.function.Predicate;
import java8.util.function.Predicates;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.List;

/**
 * Operation that removes all cards of a type from the home feed.
 *
 * @author Niko Strijbol
 */
class RemoveOperation implements FeedOperation {

    @HomeCard.CardType
    private final int cardType;
    private final Predicate<HomeCard> predicate;

    RemoveOperation(@HomeCard.CardType int cardType) {
        this(cardType, c -> c.getCardType() == cardType);
    }

    RemoveOperation(@HomeCard.CardType int cardType, Predicate<HomeCard> predicate) {
        this.cardType = cardType;
        this.predicate = predicate;
    }

    @NonNull
    @Override
    public Result<List<HomeCard>> transform(Bundle args, List<HomeCard> current) {
        return Result.Builder.fromData(StreamSupport.stream(current).filter(Predicates.negate(predicate)).collect(Collectors.toList()));
    }

    @Override
    public int getCardType() {
        return cardType;
    }

    @Override
    public String toString() {
        return "REMOVE -> Card Type " + cardType;
    }
}