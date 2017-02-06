package be.ugent.zeus.hydra.homefeed.content.urgent;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;

/**
 * @author Niko Strijbol
 */
public class UrgentRequest implements HomeFeedRequest {

    @Override
    public int getCardType() {
        return HomeCard.CardType.URGENT_FM;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest() throws RequestFailureException {
        return RefStreams.of(new UrgentCard());
    }
}