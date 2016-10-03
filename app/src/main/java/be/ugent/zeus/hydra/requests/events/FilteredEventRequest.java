package be.ugent.zeus.hydra.requests.events;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.association.Activities;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;

/**
 * A request that filters the events according to the user's preferences.
 *
 * @author Niko Strijbol
 */
public class FilteredEventRequest extends ProcessableCacheRequest<Activities, Activities> {

    public FilteredEventRequest(Context context, boolean shouldRefresh) {
        super(context, new ActivitiesRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected Activities transform(@NonNull Activities data) {
        Activities.filterActivities(data, context);
        return data;
    }
}