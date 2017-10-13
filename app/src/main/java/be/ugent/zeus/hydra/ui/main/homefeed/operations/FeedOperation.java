package be.ugent.zeus.hydra.ui.main.homefeed.operations;

import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;

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
    Result<List<HomeCard>> transform(Bundle args, List<HomeCard> current);

    /**
     * The type of card that will be added/removed by this operation.
     *
     * @return The type.
     */
    @HomeCard.CardType
    int getCardType();
}