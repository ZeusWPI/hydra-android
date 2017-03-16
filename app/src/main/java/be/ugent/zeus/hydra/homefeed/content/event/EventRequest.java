package be.ugent.zeus.hydra.homefeed.content.event;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.network.requests.association.FilteredEventRequest;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

/**
 * Home feed request for association events. We only display events between now and 1 months from now.
 *
 * @author Niko Strijbol
 */
public class EventRequest implements HomeFeedRequest {

    private final FilteredEventRequest request;

    public EventRequest(Context context, boolean shouldRefresh) {
        request = new FilteredEventRequest(context, shouldRefresh);
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.ACTIVITY;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest() throws RequestFailureException {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime plusOne = now.plusMonths(1);

        return StreamSupport.stream(request.performRequest())
                .filter(c -> c.getStart().isAfter(now) && c.getStart().isBefore(plusOne))
                .map(EventCard::new);
    }
}