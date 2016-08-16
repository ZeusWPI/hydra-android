package be.ugent.zeus.hydra.minerva.database;

import java.util.List;

/**
 * @author Niko Strijbol
 * @version 16/08/2016
 */
public interface Dao<E> {

    List<E> getAll();

}
