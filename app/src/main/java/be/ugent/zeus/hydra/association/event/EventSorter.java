package be.ugent.zeus.hydra.association.event;

import java9.util.function.Function;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import java.util.List;

/**
 * Sorts the events according to their natural ordening.
 *
 * @author Niko Strijbol
 */
class EventSorter implements Function<List<Event>, List<Event>> {
    @Override
    public List<Event> apply(List<Event> events) {
        return StreamSupport.stream(events)
                .sorted()
                .collect(Collectors.toList());
    }
}