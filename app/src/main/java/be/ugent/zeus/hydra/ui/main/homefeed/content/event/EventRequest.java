package be.ugent.zeus.hydra.ui.main.homefeed.content.event;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.requests.association.EventFilter;
import be.ugent.zeus.hydra.domain.models.association.Event;
import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.util.Arrays;
import java.util.List;

/**
 * Home feed request for association events. We only display events between now and 1 months from now.
 *
 * @author Niko Strijbol
 */
public class EventRequest implements HomeFeedRequest {

    private final Request<List<Event>> request;

    public EventRequest(Context context) {
        this.request = Requests.map(
                Requests.map(Requests.cache(context, new be.ugent.zeus.hydra.data.network.requests.association.EventRequest()), Arrays::asList),
                new EventFilter(context)
        );
    }

    @Override
    public int getCardType() {
        return Card.Type.ACTIVITY;
    }

    @NonNull
    @Override
    public Result<Stream<Card>> performRequest(Bundle args) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime plusOne = now.plusMonths(1);

        return request.performRequest(args).map(events -> StreamSupport.stream(events)
                .filter(c -> c.getStart().isAfter(now) && c.getStart().isBefore(plusOne))
                .map(EventCard::new));
    }
}