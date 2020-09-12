package be.ugent.zeus.hydra.feed.cards.debug;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;

import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;

import static be.ugent.zeus.hydra.feed.cards.Card.Type.DEBUG;

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
    public Result<Stream<Card>> execute(@NonNull Bundle args) {
        try {
            Log.i(TAG, "execute: sleep 5 seconds.");
            Thread.sleep(5000); //Sleep 5 seconds
            return Result.Builder.fromData(Stream.empty());
        } catch (InterruptedException e) {
            return Result.Builder.fromException(new RequestException(e));
        }
    }
}