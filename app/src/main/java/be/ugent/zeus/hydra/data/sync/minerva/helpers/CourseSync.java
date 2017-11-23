package be.ugent.zeus.hydra.data.sync.minerva.helpers;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.WorkerThread;

import be.ugent.zeus.hydra.data.dto.minerva.CourseMapper;
import be.ugent.zeus.hydra.data.network.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.data.sync.Synchronisation;
import be.ugent.zeus.hydra.domain.models.minerva.Course;
import be.ugent.zeus.hydra.domain.repository.CourseRepository;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import java8.util.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static be.ugent.zeus.hydra.utils.IterableUtils.transform;

/**
 * Syncs the courses for Minerva.
 *
 * @author Niko Strijbol
 */
public class CourseSync {

    private final CourseRepository courseDao;
    private final Context context;

    public CourseSync(CourseRepository courseDao, Context context) {
        this.courseDao = courseDao;
        this.context = context;
    }

    @WorkerThread
    public void synchronise(Account account) throws RequestException {

        // Get the courses from the server.
        CoursesMinervaRequest minervaRequest = new CoursesMinervaRequest(context, account);
        List<Course> serverCourses = minervaRequest.performRequest(null)
                .map(courses -> transform(courses.getCourses(), CourseMapper.INSTANCE::courseToCourse))
                .getOrThrow();

        // Get all courses in the database.
        Collection<String> existingIds = courseDao.getIds();

        // We want to keep the order of the courses.
        Map<String, Integer> orders = courseDao.getIdToOrderMap();

        for (Course newCourse: serverCourses) {
            newCourse.setOrder(Maps.getOrDefault(orders, newCourse.getId(), 0));
        }

        // Perform the actual synchronisation.
        Synchronisation<Course, String> synchronisation = new Synchronisation<>(
                existingIds,
                serverCourses,
                Course::getId
        );
        // Execute diff
        synchronisation.diff().apply(courseDao);
    }
}