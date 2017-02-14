package be.ugent.zeus.hydra.minerva.sync;

import be.ugent.zeus.hydra.minerva.database.DiffDao;
import java8.util.function.Function;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This class helps with one-way synchronisation.
 *
 * The algorithm will diff elements into three categories. See {@link Diff}.
 *
 * @param <E> The type of the elements.
 * @param <ID> The type of the primary key of the elements.
 *
 * @author Niko Strijbol
 */
public class Synchronisation<E, ID> {

    private final Diff<E, ID> diff;

    private boolean classified;

    private Function<E, ID> container;

    /**
     * Initialise a synchronisation. The algorithm assumes that the elements in these collection will be equal, even
     * if the actual data differs. For that reason you should probably pass the id's of the elements, rather then the
     * elements itself.
     *
     * If the equals method return false even though the elements are equal, the old element will be removed and the new
     * one added.
     *
     * As noted at {@link #diff()}, the underlying collections influence the complexity of this class' operations.
     * A good collection is a set.
     *
     * @param originalIds The primary keys of the data already on the device.
     * @param newData The new, fresh data from the server.
     * @param container Function to extract primary key from elements. If the elements are primary keys, pass the
     *                  identify function.
     */
    public Synchronisation(Collection<ID> originalIds, Collection<E> newData, Function<E, ID> container) {
        this.diff = new Diff<>(
                new HashSet<>(originalIds),
                new HashSet<>(),
                new HashSet<>(newData)
        );
        this.container = container;
    }

    /**
     * Sort the elements into their categories.
     *
     * The complexity of this method depends on the complexity of the underlying collections. A good suggestion are
     * sets.
     *
     * This method runs in linear time {@code O(n)}, where {@code n} is the number of elements
     * in the new data.
     *
     * @return The result.
     */
    public Diff<E, ID> diff() {

        if (classified) {
            return diff;
        }

        for (Iterator<E> it = diff.newElements.iterator(); it.hasNext();) {
            E element = it.next();
            ID id = container.apply(element);
            if (diff.staleElementsIds.contains(id)) {
                diff.updatedElements.add(element);
                diff.staleElementsIds.remove(id);
                it.remove();
            }
        }

        classified = true;
        return diff;
    }

    /**
     * The result of the diff. There are three classes: stale, updated and new.
     *
     * @param <E> The type of the elements.
     */
    public static class Diff<E, ID> {

        private final Collection<ID> staleElementsIds;
        private final Collection<E> updatedElements;
        private final Collection<E> newElements;

        private Diff(Collection<ID> staleElementsIds, Collection<E> updatedElements, Collection<E> newElements) {
            this.staleElementsIds = staleElementsIds;
            this.updatedElements = updatedElements;
            this.newElements = newElements;
        }

        /**
         * Get the elements that are on the device, but no longer on the server and should thus be removed from the
         * device.
         *
         * @return The elements. There are no guarantees about the collection.
         */
        public Collection<ID> getStaleIds() {
            return staleElementsIds;
        }

        /**
         * Get the elements that are on the device and on the server. These elements should be updated with the data
         * from the server, as the data itself might have changed.
         *
         * @return The elements. There are no guarantees about the collection.
         */
        public Collection<E> getUpdated() {
            return updatedElements;
        }

        /**
         * Get the elements that are on the server, but not yet on the device. These need to be added.
         *
         * @return The elements. There are no guarantees about the collection.
         */
        public Collection<E> getNew() {
            return newElements;
        }

        /**
         * Execute the diff automatically. This will delete, update and insert the elements, in that order.
         *
         * @param dao The
         */
        public void apply(DiffDao<E, ID> dao) {
            dao.delete(getStaleIds());
            dao.update(getUpdated());
            dao.insert(getNew());
        }
    }
}