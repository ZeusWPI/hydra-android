package be.ugent.zeus.hydra.minerva.course;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.loader.AbstractAsyncLoader;
import be.ugent.zeus.hydra.loader.LoaderException;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class CourseDaoLoader extends AbstractAsyncLoader<List<Course>> {

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
    protected List<Course> getData() throws LoaderException {
        return dao.getAll();
    }
}