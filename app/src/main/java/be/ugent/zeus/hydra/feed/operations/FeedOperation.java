package be.ugent.zeus.hydra.feed.operations;

import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.common.request.Result;

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
    Result<List<Card>> transform(Bundle args, List<Card> current);

    /**
     * The type of card that will be added/removed by this operation.
     *
     * @return The type.
     */
    @Card.Type
    int getCardType();
}