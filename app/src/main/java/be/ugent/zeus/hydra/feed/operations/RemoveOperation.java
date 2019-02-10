package be.ugent.zeus.hydra.feed.operations;

import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.common.request.Result;
import java9.util.function.Predicate;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import java.util.List;

/**
 * Operation that removes all cards of a type from the home feed.
 *
 * @author Niko Strijbol
 */
class RemoveOperation implements FeedOperation {

    @Card.Type
    private final int cardType;
    private final Predicate<Card> predicate;

    RemoveOperation(@Card.Type int cardType) {
        this(cardType, c -> c.getCardType() == cardType);
    }

    private RemoveOperation(@Card.Type int cardType, Predicate<Card> predicate) {
        this.cardType = cardType;
        this.predicate = predicate;
    }

    @NonNull
    @Override
    public Result<List<Card>> transform(Bundle args, List<Card> current) {
        return Result.Builder.fromData(StreamSupport.stream(current).filter(predicate.negate()).collect(Collectors.toList()));
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