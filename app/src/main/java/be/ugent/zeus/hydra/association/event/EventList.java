package be.ugent.zeus.hydra.association.event;

import java.util.Map;
import java.util.Objects;

/**
 * @author Niko Strijbol
 */
public final class EventList {

    private EventPage page;

    public EventList() {
    }

    public EventPage getPage() {
        return page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventList eventList = (EventList) o;
        return Objects.equals(page, eventList.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page);
    }
}
