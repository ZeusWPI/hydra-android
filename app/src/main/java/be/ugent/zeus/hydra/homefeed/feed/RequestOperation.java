package be.ugent.zeus.hydra.homefeed.feed;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
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


    @NonNull
    @Override
    public List<HomeCard> transform(final List<HomeCard> current) throws RequestFailureException {

        //Filter existing cards away.
        Stream<HomeCard> temp = StreamSupport.stream(current)
                .filter(c -> c.getCardType() != request.getCardType());

        Stream<HomeCard> temp2 = StreamSupport.stream(current)
                .filter(c -> c.getCardType() != request.getCardType());


        RefStreams.concat(temp2, request.performRequest()).sorted().collect(Collectors.toList());
        //TODO: why does concat not work here?
        //noinspection unchecked
        return RefStreams.of(temp, request.performRequest()).flatMap(x -> x)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public int getCardType() {
        return request.getCardType();
    }
}