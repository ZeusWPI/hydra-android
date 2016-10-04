package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.Activities;
import be.ugent.zeus.hydra.models.association.Activity;
import be.ugent.zeus.hydra.models.cards.AssociationActivityCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.events.ActivitiesRequest;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for events of the associations.
 *
 * @author Niko Strijbol
 */
class EventCallback extends CacheHomeLoaderCallback<Activities> {

    public EventCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    protected ActivitiesRequest getCacheRequest() {
        return new ActivitiesRequest();
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_event;
    }

    @Override
    protected List<HomeCard> convertData(@NonNull Activities data) {
        Activities.filterActivities(data, context);
        ZonedDateTime now = ZonedDateTime.now();
        List<HomeCard> list = new ArrayList<>();
        for (Activity activity: data) {
            AssociationActivityCard activityCard = new AssociationActivityCard(activity);
            if (activityCard.getPriority() > 0 && activity.getStart().isAfter(now)) {
                list.add(activityCard);
            }
        }
        return list;
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.ACTIVITY;
    }
}