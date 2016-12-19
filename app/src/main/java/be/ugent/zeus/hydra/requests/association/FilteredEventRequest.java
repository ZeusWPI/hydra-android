package be.ugent.zeus.hydra.requests.association;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.association.Events;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

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
        return new Events(StreamSupport.stream(data)
                .filter(Events.filterEvents(context))
                .collect(Collectors.toList()));
    }
}