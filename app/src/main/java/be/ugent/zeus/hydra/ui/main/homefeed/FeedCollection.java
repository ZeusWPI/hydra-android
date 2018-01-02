package be.ugent.zeus.hydra.ui.main.homefeed;

import be.ugent.zeus.hydra.ui.main.homefeed.operations.FeedOperation;
import be.ugent.zeus.hydra.utils.ExtendedSparseArray;

/**
 * @author Niko Strijbol
 */
public class FeedCollection extends ExtendedSparseArray<FeedOperation> {

    public void add(FeedOperation operation) {
        append(operation.getCardType(), operation);
    }
}
