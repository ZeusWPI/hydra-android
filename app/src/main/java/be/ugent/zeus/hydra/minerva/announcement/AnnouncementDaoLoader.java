package be.ugent.zeus.hydra.minerva.announcement;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.loaders.AbstractLoader;
import be.ugent.zeus.hydra.loaders.LoaderException;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class AnnouncementDaoLoader extends AbstractLoader<List<Announcement>> {

    private AnnouncementDao dao;
    private Course course;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    public AnnouncementDaoLoader(Context context, AnnouncementDao dao, Course course) {
        super(context);
        this.dao = dao;
        this.course = course;
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
    protected List<Announcement> loadData() throws LoaderException {
        return dao.getAnnouncementsForCourse(course, true);
    }
}