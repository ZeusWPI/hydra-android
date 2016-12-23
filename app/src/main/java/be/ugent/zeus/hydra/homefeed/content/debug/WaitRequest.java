package be.ugent.zeus.hydra.homefeed.content.debug;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.Collections;

import static be.ugent.zeus.hydra.homefeed.content.HomeCard.CardType.DEBUG;

/**
 * @author Niko Strijbol
 */
public class WaitRequest implements HomeFeedRequest {

    @Override
    public int getCardType() {
        return DEBUG;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest() throws RequestFailureException {
        try {
            Thread.sleep(10000); //Sleep 10 seconds
            return StreamSupport.stream(Collections.emptyList());
        } catch (InterruptedException e) {
            throw new RequestFailureException(e);
        }
    }
}