package be.ugent.zeus.hydra.ui.main.homefeed.content.debug;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.data.network.exceptions.PartialDataException;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.Collections;

import static be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard.CardType.DEBUG;

/**
 * @author Niko Strijbol
 */
public class WaitRequest implements HomeFeedRequest {

    private static final String TAG = "WaitRequest";

    @Override
    public int getCardType() {
        return DEBUG;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest(Bundle args) throws RequestFailureException, PartialDataException {
        try {
            Log.d(TAG, "performRequest: sleep 5 seconds.");
            Thread.sleep(5000); //Sleep 5 seconds
            return StreamSupport.stream(Collections.emptyList());
        } catch (InterruptedException e) {
            throw new RequestFailureException(e);
        }
    }
}