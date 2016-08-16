package be.ugent.zeus.hydra.loader;

import android.content.Context;

import be.ugent.zeus.hydra.minerva.database.Dao;

import java.util.List;

/**
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

    @Override
    protected List<D> getData() throws LoaderException {
        return dao.getAll();
    }
}