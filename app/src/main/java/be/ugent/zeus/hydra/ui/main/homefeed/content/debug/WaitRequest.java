package be.ugent.zeus.hydra.ui.main.homefeed.content.debug;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.Collections;

import static be.ugent.zeus.hydra.domain.models.feed.Card.Type.DEBUG;

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
    public Result<Stream<Card>> performRequest(Bundle args) {
        try {
            Log.d(TAG, "performRequest: sleep 5 seconds.");
            Thread.sleep(5000); //Sleep 5 seconds
            return Result.Builder.fromData(StreamSupport.stream(Collections.emptyList()));
        } catch (InterruptedException e) {
            return Result.Builder.fromException(new RequestException(e));
        }
    }
}