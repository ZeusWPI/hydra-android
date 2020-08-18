package be.ugent.zeus.hydra.association.list;

import android.content.Context;
import android.util.Pair;

import java.util.List;
import java.util.Map;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.AssociationListRequest;
import be.ugent.zeus.hydra.association.event.Event;
import java9.util.Objects;
import java9.util.function.Function;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import be.ugent.zeus.hydra.association.event.RawEventRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import org.threeten.bp.LocalDate;

/**
 * Data structure for a list of events. The contains an item, a header or a footer, but only one of the elements.
 * If it is an item, it contains some additional metadata.
 *
 * @author Niko Strijbol
 */
public final class EventItem {

    private final Event event;
    private final LocalDate header;
    private final Association association;

    private boolean isLastOfSection;

    private EventItem(Event event, LocalDate header, Association association) {
        this.event = event;
        this.header = header;
        this.association = association;
    }

    EventItem(Pair<Event, Association> eventPair, boolean isLastOfSection) {
        this(eventPair.first, null, eventPair.second);
        this.isLastOfSection = isLastOfSection;
    }

    EventItem(LocalDate header) {
        this(null, header, null);
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

    public Association getAssociation() {
        if (!isItem()) {
            throw new IllegalStateException("Can only be used if the EventItem contains an item.");
        }
        return association;
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
    
    public static Request<List<Pair<Event, Association>>> request(Context context) {
        Request<List<Event>> eventRequest = RawEventRequest.cachedFilteredSortedRequest(context);
        Request<Map<String, Association>> associationRequest =
                AssociationListRequest.asList(context)
                        .map(associations -> StreamSupport.stream(associations)
                                .collect(Collectors.toMap(Association::getAbbreviation, Function.identity())));
        return args -> {
            Result<List<Event>> first = eventRequest.execute(args);
            Result<Map<String, Association>> second = associationRequest.execute(args);
            return first.andThen(second).map(listMapPair -> StreamSupport.stream(listMapPair.first)
                    .map(event -> {
                        Association a = listMapPair.second.get(event.getAssociation());
                        if (a == null) {
                            a = Association.unknown();
                        }
                        return new Pair<>(event, a);
                    })
                    .collect(Collectors.toList()));
        };
    }
}
