package be.ugent.zeus.hydra.association.event.list;

import java.util.Locale;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;
import java9.util.function.BiPredicate;

/**
 * Searches a list of events for things that match.
 *
 * We currently search the title of the event, and the names of the event's association.
 *
 * We don't search the description, as search happens on the main thread, and description can become very long.
 *
 * @author Niko Strijbol
 */
class EventSearchPredicate implements BiPredicate<EventItem, String> {

    @Override
    @SuppressWarnings("RedundantIfStatement") // Ifs are clearer here
    public boolean test(EventItem eventItem, String searchTerm) {
        if (!eventItem.isItem()) {
            return true;
        }
        Event event = eventItem.getItem();
        if (event.getTitle() != null && event.getTitle().toLowerCase(Locale.getDefault()).contains(searchTerm)) {
            return true;
        }
        if (event.getAssociation() != null) {
            Association association = event.getAssociation();
            if (association.getDisplayName() != null && association.getDisplayName().toLowerCase(Locale.ROOT).contains(searchTerm)) {
                return true;
            }
            if (association.getFullName() != null && association.getFullName().toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                return true;
            }
            if (association.getInternalName().contains(searchTerm)) {
                return true;
            }
        }
        return false;
    }
}