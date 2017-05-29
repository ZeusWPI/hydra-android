package be.ugent.zeus.hydra.ui.main.homefeed.content.event;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.PartialDataException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.data.network.requests.association.FilteredEventRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.util.List;

/**
 * Home feed request for association events. We only display events between now and 1 months from now.
 *
 * @author Niko Strijbol
 */
public class EventRequest implements HomeFeedRequest {

    private final Request<List<Event>> request;

    public EventRequest(Context context, boolean shouldRefresh) {
        this.request = new FilteredEventRequest(context, new ListRequest<>(
                new CachedRequest<>(
                        context,
                        new be.ugent.zeus.hydra.data.network.requests.association.EventRequest(),
                        shouldRefresh
                )
        ));
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.ACTIVITY;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest(Bundle args) throws RequestFailureException, PartialDataException {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime plusOne = now.plusMonths(1);

        return StreamSupport.stream(request.performRequest(null))
                .filter(c -> c.getStart().isAfter(now) && c.getStart().isBefore(plusOne))
                .map(EventCard::new);
    }
}