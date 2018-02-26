package be.ugent.zeus.hydra.association.event.list;

import be.ugent.zeus.hydra.association.event.Event;
import java8.util.Objects;
import org.threeten.bp.LocalDate;

/**
 * Data structure for a list of events. The contains an item, a header or a footer, but only one of the elements.
 * If it is an item, it contains some additional metadata.
 *
 * @author Niko Strijbol
 */
class EventItem {

    private final Event event;
    private final LocalDate header;

    private boolean isFirstOfSection;
    private boolean isLastOfSection;

    private EventItem(Event event, LocalDate header) {
        this.event = event;
        this.header = header;
    }

    EventItem(Event event, boolean isFirstOfSection, boolean isLastOfSection) {
        this(event, null);
        this.isFirstOfSection = isFirstOfSection;
        this.isLastOfSection = isLastOfSection;
    }

    EventItem(LocalDate header) {
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

    void markAsLastOfSection() {
        this.isLastOfSection = true;
    }
}
