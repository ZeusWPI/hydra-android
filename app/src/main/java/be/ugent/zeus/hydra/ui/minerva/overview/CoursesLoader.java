package be.ugent.zeus.hydra.ui.minerva.overview;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderException;
import be.ugent.zeus.hydra.ui.common.loaders.BroadcastLoader;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class CoursesLoader extends BroadcastLoader<List<Course>> {

    private CourseDao dao;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    public CoursesLoader(Context context, CourseDao dao) {
        super(context, new IntentFilter(SyncBroadcast.SYNC_COURSES));
        this.dao = dao;
    }

    /**
     * Provide the data for the loader.
     *
     * @return The data.
     *
     * @throws LoaderException If the data could not be provided.
     */
    @NonNull
    @Override
    protected List<Course> loadData() throws LoaderException {
        return dao.getAll();
    }
}