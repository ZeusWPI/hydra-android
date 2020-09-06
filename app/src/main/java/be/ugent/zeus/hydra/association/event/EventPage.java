package be.ugent.zeus.hydra.association.event;

import java.util.List;
import java.util.Objects;

/**
 * @author Niko Strijbol
 */
public final class EventPage {

    private List<Event> entries;

    public List<Event> getEntries() {
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventPage eventPage = (EventPage) o;
        return Objects.equals(entries, eventPage.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }
}
