package be.ugent.zeus.hydra.feed.cards.event;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.association.Association;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.RawEventRequest;
import be.ugent.zeus.hydra.association.list.EventItem;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;

import java.util.List;

/**
 * Home feed request for association events. We only display events between now and 1 months from now.
 *
 * @author Niko Strijbol
 */
public class EventRequest extends HideableHomeFeedRequest {

    private final Request<List<Pair<Event, Association>>> request;

    public EventRequest(Context context, DismissalDao dismissalDao) {
        super(dismissalDao);
        this.request = EventItem.request(context);
    }

    @Override
    public int getCardType() {
        return Card.Type.ACTIVITY;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        return request.execute(args).map(events -> events.stream().map(EventCard::new));
    }
}