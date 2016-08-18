package be.ugent.zeus.hydra.minerva.database;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.loader.AbstractAsyncLoader;
import be.ugent.zeus.hydra.loader.LoaderException;

import java.util.List;

/**
 * Simple loader class that supports loading a dao.
 *
 * @author Niko Strijbol
 */
public class DaoLoader<D> extends AbstractAsyncLoader<List<D>> {

    private Dao<D> dao;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    public DaoLoader(Context context, Dao<D> dao) {
        super(context);
        this.dao = dao;
    }

    @NonNull
    @Override
    protected List<D> getData() throws LoaderException {
        return dao.getAll();
    }
}