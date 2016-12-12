package be.ugent.zeus.hydra.homefeed.feed;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Pair;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

import java.util.List;

/**
 * This is a simple interface that defines an operation on the home feed.
 *
 * @author Niko Strijbol
 */
public interface FeedOperation {

    /**
     * Transform the current cards to a new list. The provided list is read-only! When modifying it, a copy should
     * be returned.
     *
     * This method may be called from any thread.
     *
     * @param current The current cards.
     *
     * @return The new list, or null on error.
     */
    @NonNull
    @AnyThread
    Pair<List<HomeCard>, DiffUtil.DiffResult> transform(List<HomeCard> current) throws RequestFailureException;

    /**
     * The type of card that will be added/removed by this operation.
     *
     * @return The type.
     */
    @HomeCard.CardType
    int getCardType();
}