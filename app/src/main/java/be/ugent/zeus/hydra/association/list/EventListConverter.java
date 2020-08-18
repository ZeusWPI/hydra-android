package be.ugent.zeus.hydra.association.list;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import be.ugent.zeus.hydra.association.event.Event;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;
import org.threeten.bp.LocalDate;

/**
 * Convert events to EventItems. The list of events MUST be sorted by start date.
 */
class EventListConverter implements Function<List<Pair<Event, Association>>, List<EventItem>> {

    @Override
    public List<EventItem> apply(List<Pair<Event, Association>> events) {

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<EventItem> items = new ArrayList<>();

        // The last seen date.
        LocalDate lastDate = events.get(0).first.getStart().toLocalDate();

        // Add the first header.
        items.add(new EventItem(lastDate));
        // Add the first item
        items.add(new EventItem(events.get(0), false));

        for (int i = 1; i < events.size(); i++) {
            LocalDate date = events.get(i).first.getStart().toLocalDate();
            if (lastDate.equals(date)) {
                // This is the same date as the last one, just add the event.
                items.add(new EventItem(events.get(i), false));
            } else {
                // This is a new date.
                // We first modify the last element.
                items.get(items.size() - 1).markAsLastOfSection();
                // Add the new header.
                items.add(new EventItem(date));
                // Add the actual item
                items.add(new EventItem(events.get(i), false));
                // Set the last seen date.
                lastDate = date;
            }
        }

        // The last element is obviously also last in the list.
        items.get(items.size() - 1).markAsLastOfSection();

        return items;
    }
}
