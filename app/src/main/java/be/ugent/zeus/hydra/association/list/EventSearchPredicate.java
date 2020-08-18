package be.ugent.zeus.hydra.association.list;

import java.util.Locale;
import java.util.function.BiPredicate;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;

import be.ugent.zeus.hydra.association.event.Event;

/**
 * Searches a list of events for things that match.
 *
 * We currently search the title of the event.
 *
 * We don't search the description, as search happens on the main thread, and description can become very long.
 * 
 * TODO: replace by API search function with filters.
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
        return false;
    }
}