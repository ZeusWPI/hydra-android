package be.ugent.zeus.hydra.data.database;

import java.util.Collection;

/**
 * Interface to enable automatic execution of {@link be.ugent.zeus.hydra.data.sync.Synchronisation.Diff}.
 *
 * @param <E> The type of the objects.
 * @param <ID> The type of the primary key of the object.
 *
 * @author Niko Strijbol
 */
public interface DiffDao<E, ID> {

    /**
     * Remove all objects with the given ID's.
     *
     * @param ids The ID's of the objects to remove.
     */
    void delete(Collection<ID> ids);

    /**
     * Update all given elements.
     *
     * @param elements The elements to update.
     */
    void update(Collection<E> elements);

    /**
     * Insert all given elements.
     *
     * @param elements The elements to insert.
     */
    void insert(Collection<E> elements);
}