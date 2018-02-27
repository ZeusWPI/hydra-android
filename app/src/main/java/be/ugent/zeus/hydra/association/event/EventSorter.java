package be.ugent.zeus.hydra.association.event;

import java8.util.function.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sorts the events according to their natural ordening.
 *
 * @author Niko Strijbol
 */
class EventSorter implements Function<List<Event>, List<Event>> {

    @Override
    public List<Event> apply(List<Event> events) {
        // Not all list support editing, so make a copy.
        events = new ArrayList<>(events);
        Collections.sort(events);
        return events;
    }
}