package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.association.Activities;
import be.ugent.zeus.hydra.models.association.Activity;
import be.ugent.zeus.hydra.models.cards.AssociationActivityCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import be.ugent.zeus.hydra.requests.events.ActivitiesRequest;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Home feed request for association events.
 *
 * @author Niko Strijbol
 */
public class EventRequest extends ProcessableCacheRequest<Activities, List<HomeCard>> implements HomeFeedRequest {

    public EventRequest(Context context, boolean shouldRefresh) {
        super(context, new ActivitiesRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected List<HomeCard> transform(@NonNull Activities data) {
        Activities.filterActivities(data, context);
        ZonedDateTime now = ZonedDateTime.now();
        List<HomeCard> list = new ArrayList<>();
        for (Activity activity : data) {
            AssociationActivityCard activityCard = new AssociationActivityCard(activity);
            if (activityCard.getPriority() > 0 && activity.getStart().isAfter(now)) {
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