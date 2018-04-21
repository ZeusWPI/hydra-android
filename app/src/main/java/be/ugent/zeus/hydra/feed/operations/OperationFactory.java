package be.ugent.zeus.hydra.feed.operations;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import java8.util.function.IntPredicate;
import java8.util.function.Supplier;

/**
 * Factory to get operations on a feed.
 *
 * @author Niko Strijbol
 */
public final class OperationFactory {

    /**
     * Create an operation that will insert the request to the feed.
     *
     * @param request The request to insert.
     *
     * @return The operation.
     */
    public static FeedOperation add(HomeFeedRequest request) {
        return new RequestOperation(request);
    }

    /**
     * Create an operation that will remove a card type from the feed.
     *
     * @param cardType The type of card to remove.
     *
     * @return The operation.
     */
    public static FeedOperation del(@Card.Type int cardType) {
        return new RemoveOperation(cardType);
    }

    /**
     * Utility method to get an operation. If {@code isActive} evaluates to true, this method will call
     * {@link #add(HomeFeedRequest)}, and otherwise {@link #del(int)}.
     *
     * @param isIgnored If the card type should be ignored or not. Must return true if the card must be ignored.
     * @param supplier The request supplier. Use a supplier so the request only gets made when needed.
     * @param type     Type of the home card.
     *
     * @return The operation.
     */
    public static FeedOperation get(IntPredicate isIgnored, Supplier<HomeFeedRequest> supplier, @Card.Type int type) {
        if (isIgnored.test(type)) {
            return del(type);
        } else {
            return add(supplier.get());
        }
    }
}