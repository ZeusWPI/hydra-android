package be.ugent.zeus.hydra.association.list;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.AssociationListRequest;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.RawEventRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * Data structure for a list of events. The contains an item, a header or a footer, but only one of the elements.
 * If it is an item, it contains some additional metadata.
 *
 * @author Niko Strijbol
 */
public final class EventItem {

    private static final String TAG = "EventItem";

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

    public static Request<Pair<List<Pair<Event, Association>>, List<Association>>> request(Context context, Filter filter) {
        Request<List<Event>> eventRequest = RawEventRequest.create(context, filter);
        Request<Map<String, Association>> associationRequest =
                AssociationListRequest.asList(context)
                        .map(associations -> associations.stream()
                                .collect(Collectors.toMap(Association::getAbbreviation, Function.identity())));
        return args -> {
            Result<List<Event>> first = eventRequest.execute(args);
            Result<Map<String, Association>> second = associationRequest.execute(args);
            return first.andThen(second).map(listMapPair -> listMapPair.first.stream()
                    .map(event -> {
                        Association a = listMapPair.second.get(event.getAssociation());
                        if (a == null) {
                            Log.w(TAG, "request: unknown assoc: " + event.getAssociation());
                            a = Association.unknown(event.getAssociation());
                        }
                        return new Pair<>(event, a);
                    })
                    .collect(Collectors.toList()))
                    .andThen(second.map(stringAssociationMap -> new ArrayList<>(stringAssociationMap.values())));
        };
    }
}
