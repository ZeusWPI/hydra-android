package be.ugent.zeus.hydra.feed.cards.implementations.urgent;

import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import java9.util.stream.Stream;

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