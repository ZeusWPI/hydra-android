package be.ugent.zeus.hydra.feed.cards.urgent;

import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * @author Niko Strijbol
 */
public class UrgentRequest implements HomeFeedRequest {

    @Override
    public int getCardType() {
        return Card.Type.URGENT_FM;
    }

    @NonNull
    @Override
    public Result<Stream<Card>> execute(@NonNull Bundle args) {
        return Result.Builder.fromData(Stream.of(new UrgentCard()));
    }
}