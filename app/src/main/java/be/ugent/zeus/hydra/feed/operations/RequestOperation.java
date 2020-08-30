package be.ugent.zeus.hydra.feed.operations;

import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * An operation that adds items to the home feed.
 *
 * @author Niko Strijbol
 */
class RequestOperation implements FeedOperation {

    private final HomeFeedRequest request;

    RequestOperation(HomeFeedRequest request) {
        this.request = request;
    }

    /**
     * This methods removes all card instances of this operation's card type, performs the request and adds the results
     * back to the list.
     *
     * This means that while the cards may be logically equal, they will not be the same instance.
     *
     * @param current The current cards.
     *
     * @return The updates cards.
     */
    @NonNull
    @Override
    public Result<List<Card>> transform(Bundle args, final List<Card> current) {

        // Filter existing cards away.
        Stream<Card> temp = current.stream()
                .filter(c -> c.getCardType() != request.getCardType());

        return request.execute(args).map(homeCardStream ->
                Stream.concat(temp, homeCardStream).sorted().collect(Collectors.toList())
        );
    }

    @Override
    public int getCardType() {
        return request.getCardType();
    }

    @Override
    public String toString() {
        return "REQUEST -> Card Type " + request.getCardType();
    }
}
