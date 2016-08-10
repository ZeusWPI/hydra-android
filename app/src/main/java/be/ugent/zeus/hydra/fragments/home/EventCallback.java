package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.association.Activities;
import be.ugent.zeus.hydra.models.association.Activity;
import be.ugent.zeus.hydra.models.cards.AssociationActivityCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.ActivitiesRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Callback for events of the associations.
 *
 * @author Niko Strijbol
 */
class EventCallback extends HomeLoaderCallback<Activities> {

    public EventCallback(Context context, HomeCardAdapter adapter, ProgressCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    protected ActivitiesRequest getCacheRequest() {
        return new ActivitiesRequest();
    }

    @Override
    protected List<HomeCard> convertData(@NonNull Activities data) {
        List<Activity> filteredAssociationActivities = Activities.getPreferredActivities(data, context);
        Date date = new Date();
        List<HomeCard> list = new ArrayList<>();
        for (Activity activity: filteredAssociationActivities) {
            AssociationActivityCard activityCard = new AssociationActivityCard(activity);
            if(activityCard.getPriority() > 0 && activity.getEndDate().after(date)) {
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