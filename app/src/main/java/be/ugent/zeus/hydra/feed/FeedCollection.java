package be.ugent.zeus.hydra.feed;

import be.ugent.zeus.hydra.feed.operations.FeedOperation;
import be.ugent.zeus.hydra.common.ExtendedSparseArray;

/**
 * @author Niko Strijbol
 */
public class FeedCollection extends ExtendedSparseArray<FeedOperation> {

    public void add(FeedOperation operation) {
        append(operation.getCardType(), operation);
    }
}
