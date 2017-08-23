package be.ugent.zeus.hydra.ui.main.homefeed.operations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Collectors;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.List;

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
     * @throws RequestException If the request fails.
     */
    @NonNull
    @Override
    public List<HomeCard> transform(Bundle args, final List<HomeCard> current) throws RequestException {

        // Filter existing cards away.
        Stream<HomeCard> temp = StreamSupport.stream(current)
                .filter(c -> c.getCardType() != request.getCardType());

        Stream<HomeCard> requestStream = request.performRequest(args).getDataOrThrow();
        return RefStreams.concat(temp, requestStream).sorted().collect(Collectors.toList());
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