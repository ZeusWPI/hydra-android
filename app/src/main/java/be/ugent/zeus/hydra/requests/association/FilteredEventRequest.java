package be.ugent.zeus.hydra.requests.association;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.association.Events;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;

/**
 * A request that filters the events according to the user's preferences.
 *
 * @author Niko Strijbol
 */
public class FilteredEventRequest extends ProcessableCacheRequest<Events, Events> {

    public FilteredEventRequest(Context context, boolean shouldRefresh) {
        super(context, new EventRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected Events transform(@NonNull Events data) {
        Events.filterEvents(data, context);
        return data;
    }
}