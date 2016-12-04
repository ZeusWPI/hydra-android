package be.ugent.zeus.hydra.homefeed.feed;

import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import java8.util.function.Function;
import java8.util.function.Predicate;
import java8.util.function.Supplier;

/**
 * Factory to create operations on a feed.
 *
 * @author Niko Strijbol
 */
public final class OperationFactory {

    /**
     * Create an operation that will add the request to the feed.
     *
     * @param request The request to add.
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
    public static FeedOperation del(@HomeCard.CardType int cardType) {
        return new RemoveOperation(cardType);
    }

    /**
     * Create an operation that will remove something from the home feed.
     *
     * @param cardType The type of card to remove.
     *
     * @return The operation.
     */
    public static FeedOperation del(@HomeCard.CardType int cardType, Predicate<HomeCard> predicate) {
        return new RemoveOperation(cardType, predicate);
    }

    /**
     * Utility method to get an operation. If {@code isActive} evaluates to true, this method will call
     * {@link #add(HomeFeedRequest)}, and otherwise {@link #del(int)}.
     *
     * @param isActive If the type is active or not.
     * @param supplier The request supplier. Use a supplier so the request only gets made when needed.
     * @param type     Type of the home card.
     *
     * @return The operation.
     */
    public static FeedOperation get(Function<Integer, Boolean> isActive, Supplier<HomeFeedRequest> supplier, @HomeCard.CardType int type) {
        if (isActive.apply(type)) {
            return add(supplier.get());
        } else {
            return del(type);
        }
    }
}