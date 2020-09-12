package be.ugent.zeus.hydra.association.list;

import android.content.Context;
import android.util.Pair;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import be.ugent.zeus.hydra.association.AssociationListRequest;
import be.ugent.zeus.hydra.association.AssociationMap;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.RawEventRequest;
import be.ugent.zeus.hydra.common.request.Request;

/**
 * Data structure for a list of events. The contains an item, a header or a footer, but only one of the elements.
 * If it is an item, it contains some additional metadata.
 *
 * @author Niko Strijbol
 */
public final class EventItem implements Comparable<EventItem> {

    private final Event event;
    private final LocalDate header;

    private boolean isLastOfSection;

    private EventItem(Event event, LocalDate header) {
        this.event = event;
        this.header = header;
    }

    EventItem(Event event, boolean isLastOfSection) {
        this(event, null);
        this.isLastOfSection = isLastOfSection;
    }

    EventItem(LocalDate header) {
        this(null, header);
    }

    public static Request<Pair<List<EventItem>, AssociationMap>> request(Context context, Filter filter) {
        return RawEventRequest.create(context, filter)
                .map(new EventListConverter())
                .andThen(AssociationListRequest.create(context));
    }

    public boolean isHeader() {
        return header != null;
    }

    public boolean isItem() {
        return event != null;
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
        return isLastOfSection == eventItem.isLastOfSection &&
                Objects.equals(event, eventItem.event) &&
                Objects.equals(header, eventItem.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, header, isLastOfSection);
    }

    void markAsLastOfSection() {
        this.isLastOfSection = true;
    }
    
    public OffsetDateTime getDate() {
        if (isItem()) {
            return getItem().getStart();
        } else {
            return getHeader().atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        }
    }

    @Override
    public int compareTo(EventItem o) {
        return getDate().compareTo(o.getDate());
    }
}
