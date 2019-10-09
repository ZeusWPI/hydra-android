package be.ugent.zeus.hydra.feed.cards.event;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.RawEventRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import java9.util.stream.Stream;
import java9.util.stream.StreamSupport;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

/**
 * Home feed request for association events. We only display events between now and 1 months from now.
 *
 * @author Niko Strijbol
 */
public class EventRequest extends HideableHomeFeedRequest {

    private final Request<List<Event>> request;

    public EventRequest(Context context, DismissalDao dismissalDao) {
        super(dismissalDao);
        this.request = RawEventRequest.cachedFilteredSortedRequest(context);
    }

    @Override
    public int getCardType() {
        return Card.Type.ACTIVITY;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime plusOne = now.plusMonths(1);

        return request.execute(args).map(events -> StreamSupport.stream(events)
                .filter(c -> c.getStart().isAfter(now) && c.getStart().isBefore(plusOne))
                .map(EventCard::new));
    }
}