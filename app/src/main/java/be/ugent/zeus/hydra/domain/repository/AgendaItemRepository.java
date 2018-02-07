package be.ugent.zeus.hydra.domain.repository;


import be.ugent.zeus.hydra.domain.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.utils.ExtendedSparseArray;
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
     * @param lower  The lower bound, inclusive.
     * @param higher The upper bound, exclusive.
     *
     * @return The results.
     */
    List<AgendaItem> getBetween(OffsetDateTime lower, OffsetDateTime higher);

    /**
     * Get a map of all calendar items, mapping the item's id to the calendar id. The actual returned object
     * is a sparse array for performance reasons. If Java (and Android) ever introduces a Map with primitive types,
     * we'll use that instead.
     *
     * @return The map of all items.
     */
    ExtendedSparseArray<Long> getIdsAndCalendarIds();

    List<Long> getCalendarIdsForIds(Collection<Integer> agendaIds);
}