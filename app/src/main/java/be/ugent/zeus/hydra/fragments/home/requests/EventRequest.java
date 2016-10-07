package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.models.association.Events;
import be.ugent.zeus.hydra.models.cards.AssociationActivityCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Home feed request for association events.
 *
 * @author Niko Strijbol
 */
public class EventRequest extends ProcessableCacheRequest<Events, List<HomeCard>> implements HomeFeedRequest {

    public EventRequest(Context context, boolean shouldRefresh) {
        super(context, new be.ugent.zeus.hydra.requests.association.EventRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected List<HomeCard> transform(@NonNull Events data) {
        Events.filterEvents(data, context);
        ZonedDateTime now = ZonedDateTime.now();
        List<HomeCard> list = new ArrayList<>();
        for (Event event : data) {
            AssociationActivityCard activityCard = new AssociationActivityCard(event);
            if (activityCard.getPriority() > 0 && event.getStart().isAfter(now)) {
                list.add(activityCard);
            }
        }
        return list;
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.ACTIVITY;
    }
}