package be.ugent.zeus.hydra.fragments.home.operations;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Pair;
import be.ugent.zeus.hydra.fragments.home.loader.HomeDiffCallback;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

import java.util.ArrayList;
import java.util.List;

/**
 * Operation that removes all cards of a type from the home feed.
 *
 * @author Niko Strijbol
 */
public class RemoveOperation implements FeedOperation {

    @HomeCard.CardType
    private final int cardType;

    public RemoveOperation(@HomeCard.CardType int cardType) {
        this.cardType = cardType;
    }

    /**
     * Shorter helper function.
     */
    public static RemoveOperation del(@HomeCard.CardType int cardType) {
        return new RemoveOperation(cardType);
    }

    @NonNull
    @Override
    public Pair<List<HomeCard>, DiffUtil.DiffResult> transform(List<HomeCard> current) throws RequestFailureException {

        final List<HomeCard> newData = new ArrayList<>(current);

        FeedUtils.remove(newData, cardType);

        final DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new HomeDiffCallback(current, newData), true);

        return new Pair<>(newData, diff);
    }

    @Override
    public int getCardType() {
        return cardType;
    }
}