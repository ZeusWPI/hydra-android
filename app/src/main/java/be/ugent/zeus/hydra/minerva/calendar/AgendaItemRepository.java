package be.ugent.zeus.hydra.minerva.calendar;


import be.ugent.zeus.hydra.common.ExtendedSparseArray;
import be.ugent.zeus.hydra.common.FullRepository;
import org.threeten.bp.OffsetDateTime;

import java.util.Collection;
import java.util.List;

/**
 * Provides access to {@link AgendaItem}s.
 *
 * @author Niko Strijbol
 */
public interface AgendaItemRepository extends FullRepository<Integer, AgendaItem> {

    /**
     * Get all future or ongoing calendar items associated with a certain course.
     *
     * @param courseId   The ID of the course.
     *
     * @return The items.
     */
    List<AgendaItem> getAllForCourseFuture(String courseId, OffsetDateTime now);

    /**
     * Get all items between two dates. The lower date is inclusive, the upper date is exclusive. More formal, we can
     * express it as {@code ∀ item ∈ results: lower ≤ item.date < upper}.
     *
     * This will only return events from courses that don't have the "calendar ignore" flag set, so:
     * {@code ∀ item ∈ results: lower ≤ item.date < upper ∧ ¬IgnoresCalendar(item)}
     *
     * @param lower  The lower bound, inclusive.
     * @param higher The upper bound, exclusive.
     *
     * @return The results.
     */
    List<AgendaItem> getBetweenNonIgnored(OffsetDateTime lower, OffsetDateTime higher);

    /**
     * Get a map of all calendar items, mapping the item's id to the calendar id. The actual returned object
     * is a sparse array for performance reasons. If Java (and Android) ever introduces a Map with primitive types,
     * we'll use that instead.
     *
     * @return The map of all items.
     */
    ExtendedSparseArray<Long> getIdsAndCalendarIds();

    /**
     * Get a list of device calender ID's for the items with the provided ID's. The resulting list is a one-on-one
     * mapping between the item ID's and the calendar ID's, e.g. the first item in the resulting list is for the first
     * item in the provided list.
     *
     * @param agendaIds The ID's for the items for which we want the calendar ID.
     *
     * @return One-on-one mapping of the calendar ID's.
     */
    List<Long> getCalendarIdsForIds(Collection<Integer> agendaIds);
}