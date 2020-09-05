package be.ugent.zeus.hydra.association.list;

import android.util.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;

/**
 * Convert events to EventItems. The list of events MUST be sorted by start date.
 */
class EventListConverter implements Function<Pair<List<Pair<Event, Association>>,
        List<Association>>, Pair<List<EventItem>, List<Association>>> {

    @Override
    public Pair<List<EventItem>, List<Association>> apply(Pair<List<Pair<Event, Association>>, List<Association>> events) {

        if (events.first.isEmpty()) {
            return new Pair<>(Collections.emptyList(), events.second);
        }

        List<EventItem> items = new ArrayList<>();

        // The last seen date.
        LocalDate lastDate = events.first.get(0).first.getStart().toLocalDate();

        // Add the first header.
        items.add(new EventItem(lastDate));
        // Add the first item
        items.add(new EventItem(events.first.get(0), false));

        for (int i = 1; i < events.first.size(); i++) {
            LocalDate date = events.first.get(i).first.getStart().toLocalDate();
            if (lastDate.equals(date)) {
                // This is the same date as the last one, just add the event.
                items.add(new EventItem(events.first.get(i), false));
            } else {
                // This is a new date.
                // We first modify the last element.
                items.get(items.size() - 1).markAsLastOfSection();
                // Add the new header.
                items.add(new EventItem(date));
                // Add the actual item
                items.add(new EventItem(events.first.get(i), false));
                // Set the last seen date.
                lastDate = date;
            }
        }

        // The last element is obviously also last in the list.
        items.get(items.size() - 1).markAsLastOfSection();

        return new Pair<>(items, events.second);
    }
}
