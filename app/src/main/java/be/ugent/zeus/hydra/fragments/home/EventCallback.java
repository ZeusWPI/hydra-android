package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.association.Activities;
import be.ugent.zeus.hydra.models.association.Activity;
import be.ugent.zeus.hydra.models.cards.AssociationActivityCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import be.ugent.zeus.hydra.requests.events.ActivitiesRequest;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for events of the associations.
 *
 * @author Niko Strijbol
 */
class EventCallback extends AbstractCallback {

    public EventCallback(HomeFragment fragment, HomeCardAdapter adapter) {
        super(fragment, adapter);
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_event;
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.ACTIVITY;
    }

    @Override
    public Loader<ThrowableEither<List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        return new RequestAsyncTaskLoader<>(new EventRequest(context, fragment.shouldRefresh()), context);
    }

    private class EventRequest extends ProcessableCacheRequest<Activities, List<HomeCard>> {

        private EventRequest(Context context, boolean shouldRefresh) {
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
    }
}