package be.ugent.zeus.hydra.homefeed.feed;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Pair;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.loader.HomeDiffCallback;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
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
    public Pair<List<HomeCard>, DiffUtil.DiffResult> transform(List<HomeCard> current) throws RequestFailureException {

        List<HomeCard> newList = StreamSupport.stream(current).filter(Predicates.negate(predicate)).collect(Collectors.toList());

        //Calculate diff
        final DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new HomeDiffCallback(current, newList), true);

        return new Pair<>(newList, diff);
    }

    @Override
    public int getCardType() {
        return cardType;
    }
}