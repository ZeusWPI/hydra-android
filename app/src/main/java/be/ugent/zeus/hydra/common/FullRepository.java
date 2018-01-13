package be.ugent.zeus.hydra.common;

import java.util.Collection;
import java.util.List;

/**
 * Represents the basics of a full repository. A full repository is a repository that contains at least the basic
 * query/update/delete methods.
 *
 * @author Niko Strijbol
 */
public interface FullRepository<ID, M> {

    /**
     * Get one object.
     *
     * @param id The ID of the object.
     *
     * @return The object.
     */
    M getOne(ID id);

    /**
     * Get all objects.
     *
     * @return All objects.
     */
    List<M> getAll();

    /**
     * Insert one. The ID and other datastore generated fields such as last updated must be set after the object has
     * been inserted.
     *
     * @param object The object to insert. The object may be modified.
     */
    void insert(M object);

    /**
     * Insert multiple objects. If the collection is ordered, the objects will be inserted in order. The ID and other
     * datastore generated fields such as last updated must be set after the objects have been inserted.
     *
     * @param objects The objects to insert. The objects may be modified.
     */
    void insert(Collection<M> objects);

    /**
     * Update one object.
     *
     * @param object The object to update. The object may be modified.
     */
    void update(M object);

    /**
     * Update multiple objects.
     *
     * @param objects The objects to update. The objects may be modified.
     */
    void update(Collection<M> objects);

    /**
     * Delete an object. After deletion, the object instance may not be valid anymore.
     *
     * @param object The object to delete.
     */
    void delete(M object);

    /**
     * Delete an object by its ID.
     *
     * @param id The ID of the object to delete.
     */
    void deleteById(ID id);

    /**
     * Delete an object by its ID.
     *
     * @param id The ID of the object to delete.
     */
    void deleteById(Collection<ID> id);

    /**
     * Delete all objects in the database.
     */
    void deleteAll();

    /**
     * Delete multiple objects. After deletion, the object instance may not be valid anymore.
     *
     * @param objects The objects to delete.
     */
    void delete(Collection<M> objects);
}