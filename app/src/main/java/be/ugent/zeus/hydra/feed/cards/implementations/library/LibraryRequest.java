package be.ugent.zeus.hydra.feed.cards.implementations.library;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java9.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.implementations.urgent.UrgentCard;

/**
 * @author Niko Strijbol
 */
public class LibraryRequest implements HomeFeedRequest {

    private final Context context;

    public LibraryRequest(Context context) {
        this.context = context;
    }

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