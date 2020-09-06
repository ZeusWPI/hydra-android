package be.ugent.zeus.hydra.association.list;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.association.event.Event;

/**
 * Convert events to EventItems. The list of events MUST be sorted by start date.
 */
class EventListConverter implements Function<List<Event>, List<EventItem>> {

    @Override
    public List<EventItem> apply(List<Event> events) {

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        return events.stream()
                .collect(Collectors.groupingBy(event -> event.getStart().toLocalDate()))
                .entrySet()
                .stream()
                .flatMap(this::convert)
                .sorted()
                .collect(Collectors.toList());
    }

    private Stream<EventItem> convert(Map.Entry<LocalDate, List<Event>> entry) {
        List<Event> list = entry.getValue();
        EventItem header = new EventItem(entry.getKey());
        Stream<EventItem> events = list.subList(0, list.size() - 1).stream().map(event -> new EventItem(event, false));
        EventItem last = new EventItem(list.get(list.size() - 1), true);

        return Stream.concat(
                Stream.of(header),
                Stream.concat(events, Stream.of(last))
        );
    }
}
