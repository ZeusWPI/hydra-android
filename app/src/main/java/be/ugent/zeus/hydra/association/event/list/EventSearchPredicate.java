package be.ugent.zeus.hydra.association.event.list;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;
import java8.util.function.BiPredicate;

/**
 * Searches a list of events for things that match.
 *
 * We currently search the title of the event, and the names of the event's association.
 *
 * We don't search the description, as search happens on the main thread, and description can become very long.
 *
 * @author Niko Strijbol
 */
class EventSearchPredicate implements BiPredicate<Event, String> {

    @Override
    @SuppressWarnings("RedundantIfStatement") // Ifs are clearer here
    public boolean test(Event event, String searchTerm) {
        if (event.getTitle() != null && event.getTitle().toLowerCase().contains(searchTerm)) {
            return true;
        }
        if (event.getAssociation() != null) {
            Association association = event.getAssociation();
            if (association.getDisplayName() != null && association.getDisplayName().toLowerCase().contains(searchTerm)) {
                return true;
            }
            if (association.getFullName() != null && association.getFullName().toLowerCase().contains(searchTerm)) {
                return true;
            }
            if (association.getInternalName().contains(searchTerm)) {
                return true;
            }
        }
        return false;
    }
}