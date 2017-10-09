package be.ugent.zeus.hydra.data.sync.minerva.helpers;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.WorkerThread;

import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.models.minerva.Courses;
import be.ugent.zeus.hydra.data.network.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.data.sync.Synchronisation;
import be.ugent.zeus.hydra.repository.requests.RequestException;

import java.util.Collection;

/**
 * Syncs the courses for Minerva.
 *
 * @author Niko Strijbol
 */
public class CourseSync {

    private final CourseDao courseDao;
    private final Context context;

    public CourseSync(CourseDao courseDao, Context context) {
        this.courseDao = courseDao;
        this.context = context;
    }

    @WorkerThread
    public void synchronise(Account account) throws RequestException {

        // Get the courses from the server.
        CoursesMinervaRequest minervaRequest = new CoursesMinervaRequest(context, account);
        Courses serverCourses = minervaRequest.performRequest(null).getOrThrow();

        // Get all courses in the database.
        Collection<String> existingIds = courseDao.getIds();

        // Perform the actual synchronisation.
        Synchronisation<Course, String> synchronisation = new Synchronisation<>(
                existingIds,
                serverCourses.getCourses(),
                Course::getId
        );
        // Execute diff
        synchronisation.diff().apply(courseDao);
    }
}