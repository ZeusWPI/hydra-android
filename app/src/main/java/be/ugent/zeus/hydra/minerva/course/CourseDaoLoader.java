package be.ugent.zeus.hydra.minerva.course;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.loaders.AbstractLoader;
import be.ugent.zeus.hydra.loaders.LoaderException;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class CourseDaoLoader extends AbstractLoader<List<Course>> {

    private CourseDao dao;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    public CourseDaoLoader(Context context, CourseDao dao) {
        super(context);
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