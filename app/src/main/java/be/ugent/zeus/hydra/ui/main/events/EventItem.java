package be.ugent.zeus.hydra.ui.main.events;

import be.ugent.zeus.hydra.data.models.association.Event;
import java8.util.Objects;
import java8.util.function.Function;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data structure for a list of events. The contains an item, a header or a footer, but only one of the elements.
 * If it is an item, it contains some additional metadata.
 *
 * @author Niko Strijbol
 */
public class EventItem {

    private final Event event;
    private final LocalDate header;

    private boolean isFirstOfSection;
    private boolean isLastOfSection;

    private EventItem(Event event, LocalDate header) {
        this.event = event;
        this.header = header;
    }

    public EventItem(Event event, boolean isFirstOfSection, boolean isLastOfSection) {
        this(event, null);
        this.isFirstOfSection = isFirstOfSection;
        this.isLastOfSection = isLastOfSection;
    }

    public EventItem(LocalDate header) {
        this(null, header);
    }

    public boolean isHeader() {
        return header != null;
    }

    public boolean isItem() {
        return event != null;
    }

    public boolean isFirstOfSection() {
        if (!isItem()) {
            throw new IllegalStateException("Can only be used if the EventItem contains an item.");
        }
        return isFirstOfSection;
    }

    public boolean isLastOfSection() {
        if (!isItem()) {
            throw new IllegalStateException("Can only be used if the EventItem contains an item.");
        }
        return isLastOfSection;
    }

    public Event getItem() {
        if (!isItem()) {
            throw new IllegalStateException("Can only be used if the EventItem contains an item.");
        }
        return event;
    }

    public LocalDate getHeader() {
        if (!isHeader()) {
            throw new IllegalStateException("Can only be used if the EventItem contains a header.");
        }
        return header;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventItem eventItem = (EventItem) o;
        return isFirstOfSection == eventItem.isFirstOfSection &&
                isLastOfSection == eventItem.isLastOfSection &&
                Objects.equals(event, eventItem.event) &&
                Objects.equals(header, eventItem.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, header, isFirstOfSection, isLastOfSection);
    }

    private void setLastOfSection(boolean isLastOfSection) {
        this.isLastOfSection = isLastOfSection;
    }

    /**
     * Convert events to EventItems. The list of events MUST be sorted by start date.
     */
    public static class Converter implements Function<List<Event>, List<EventItem>> {

        @Override
        public List<EventItem> apply(List<Event> events) {

            if (events.isEmpty()) {
                return Collections.emptyList();
            }

            List<EventItem> items = new ArrayList<>();

            // The last seen date.
            LocalDate lastDate = events.get(0).getStart().toLocalDate();

            // Add the first header.
            items.add(new EventItem(lastDate));
            // Add the first item
            items.add(new EventItem(events.get(0), true, false));

            for (int i = 1; i < events.size(); i++) {
                LocalDate date = events.get(i).getStart().toLocalDate();
                if (lastDate.equals(date)) {
                    // This is the same date as the last one, just add the event.
                    items.add(new EventItem(events.get(i), false, false));
                } else {
                    // This is a new date.
                    // We first modify the last element.
                    items.get(items.size() - 1).setLastOfSection(true);
                    // Add the new header.
                    items.add(new EventItem(date));
                    // Add the actual item
                    items.add(new EventItem(events.get(i), true, false));
                    // Set the last seen date.
                    lastDate = date;
                }
            }

            // The last element is obviously also last in the list.
            items.get(items.size() - 1).setLastOfSection(true);

            return items;
        }
    }
}
