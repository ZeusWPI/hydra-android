package be.ugent.zeus.hydra.association.event;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Sorts the events according to their natural ordening.
 *
 * @author Niko Strijbol
 */
class EventSorter implements Function<List<Event>, List<Event>> {
    @Override
    public List<Event> apply(List<Event> events) {
        return events.stream()
                .sorted()
                .collect(Collectors.toList());
    }
}