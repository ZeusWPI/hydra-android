package be.ugent.zeus.hydra.ui.main.homefeed.content.specialevent;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.data.network.requests.SpecialRemoteEventRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Request wrapper to limit the number of requests that are shown.
 *
 * @author Niko Strijbol
 */
public class SpecialEventRequest implements HomeFeedRequest {

    private final SpecialRemoteEventRequest remoteEventRequest;

    public SpecialEventRequest(Context context, boolean shouldRefresh) {
        remoteEventRequest = new SpecialRemoteEventRequest(context,
                new CachedRequest<>(context, new be.ugent.zeus.hydra.data.network.requests.SpecialEventRequest(), shouldRefresh)
        );
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest() throws RequestFailureException {
        List<HomeCard> list = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now();
        for (SpecialEvent event : remoteEventRequest.performRequest().getSpecialEvents()) {

            //Events without date are always shown.
            if (event.getStart() == null && event.getEnd() == null) {
                list.add(new SpecialEventCard(event));
            } else {
                if ((event.getStart().isBefore(now) && event.getEnd().isAfter(now)) || (BuildConfig.DEBUG && event.isDevelopment())) {
                    list.add(new SpecialEventCard(event));
                }
            }
        }
        return StreamSupport.stream(list);
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.SPECIAL_EVENT;
    }
}