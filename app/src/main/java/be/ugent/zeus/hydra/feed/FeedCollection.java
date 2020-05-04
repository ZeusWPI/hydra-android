package be.ugent.zeus.hydra.feed;

import be.ugent.zeus.hydra.feed.operations.FeedOperation;
import be.ugent.zeus.hydra.common.ExtendedSparseArray;

/**
 * A dict or {@link java.util.Map}-like data structure that maps the {@link be.ugent.zeus.hydra.feed.cards.Card.Type}
 * to the operation that affects that type.
 * <br>
 * This class is an extended version of {@link ExtendedSparseArray}, with some handy methods.
 *
 * @author Niko Strijbol
 */
class FeedCollection extends ExtendedSparseArray<FeedOperation> {

    /**
     * @param operation The operation to add to the mapping.
     */
    public void add(FeedOperation operation) {
        append(operation.getCardType(), operation);
    }
}
