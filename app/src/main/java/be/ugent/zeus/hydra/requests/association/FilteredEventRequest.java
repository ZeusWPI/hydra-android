package be.ugent.zeus.hydra.requests.association;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.models.association.Events;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.Comparator;

/**
 * A request that filters the events according to the user's preferences, and sorted according to a given
 * comparator.
 *
 * @author Niko Strijbol
 */
public class FilteredEventRequest extends ProcessableCacheRequest<Events, Events> {

    private Comparator<Event> comparator;

    public FilteredEventRequest(Context context, boolean shouldRefresh) {
        super(context, new EventRequest(), shouldRefresh);
        this.comparator = (o1, o2) -> 0;
    }

    public FilteredEventRequest(Context context, boolean shouldRefresh, Comparator<Event> comparator) {
        super(context, new EventRequest(), shouldRefresh);
        this.comparator = comparator;
    }

    @NonNull
    @Override
    protected Events transform(@NonNull Events data) {
        return new Events(StreamSupport.stream(data)
                .filter(Events.filterEvents(context))
                .sorted(comparator)
                .collect(Collectors.toList()));
    }
}