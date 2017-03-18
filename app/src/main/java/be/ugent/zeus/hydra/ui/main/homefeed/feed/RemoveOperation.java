package be.ugent.zeus.hydra.ui.main.homefeed.feed;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
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
    public List<HomeCard> transform(List<HomeCard> current) throws RequestFailureException {
        return StreamSupport.stream(current).filter(Predicates.negate(predicate)).collect(Collectors.toList());
    }

    @Override
    public int getCardType() {
        return cardType;
    }
}