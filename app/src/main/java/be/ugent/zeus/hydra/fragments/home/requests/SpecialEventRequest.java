package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.requests.SpecialRemoteEventRequest;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SpecialEventRequest implements Request<List<HomeCard>>, HomeFeedRequest {

    private final SpecialRemoteEventRequest remoteEventRequest;

    public SpecialEventRequest(Context context, boolean shouldRefresh) {
        remoteEventRequest = new SpecialRemoteEventRequest(context, shouldRefresh);
    }

    @NonNull
    @Override
    public List<HomeCard> performRequest() throws RequestFailureException {
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
        return list;
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.SPECIAL_EVENT;
    }
}
