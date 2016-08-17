package be.ugent.zeus.hydra.minerva.database;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * A dao (data access object) provides access to data without knowing the implementations.
 *
 * @author Niko Strijbol
 */
public interface Dao<E> {

    /**
     * @return All elements in this dao.
     */
    @NonNull
    List<E> getAll();
}