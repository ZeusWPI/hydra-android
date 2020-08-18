package be.ugent.zeus.hydra.association.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Remove headers that have no more events.
 *
 * @author Niko Strijbol
 */
public class EventSearchFilter implements Function<List<EventItem>, List<EventItem>> {

    @Override
    public List<EventItem> apply(List<EventItem> events) {

        if (events.isEmpty()) {
            return events;
        }

        List<EventItem> filtered = new ArrayList<>();

        for (int i = 0; i < events.size() - 1; i++) {
            EventItem current = events.get(i);
            if (current.isItem()) {
                filtered.add(current);
            } else {
                if (!events.get(i + 1).isHeader()) {
                    filtered.add(current);
                }
            }
        }

        if (events.get(events.size() - 1).isItem()) {
            filtered.add(events.get(events.size() - 1));
        }

        return filtered;
    }
}
