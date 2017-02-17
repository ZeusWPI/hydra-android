package be.ugent.zeus.hydra.homefeed.loader;

import be.ugent.zeus.hydra.homefeed.feed.FeedOperation;
import be.ugent.zeus.hydra.utils.IterableSparseArray;

/**
 * @author Niko Strijbol
 */
public class FeedCollection extends IterableSparseArray<FeedOperation> {

    public void add(FeedOperation operation) {
        append(operation.getCardType(), operation);
    }
}
