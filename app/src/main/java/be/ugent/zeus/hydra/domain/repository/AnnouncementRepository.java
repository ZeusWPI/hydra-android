package be.ugent.zeus.hydra.domain.repository;

import be.ugent.zeus.hydra.domain.models.minerva.Announcement;
import be.ugent.zeus.hydra.domain.models.minerva.Course;
import org.threeten.bp.ZonedDateTime;

import java.util.List;
import java.util.Map;

/**
 * @author Niko Strijbol
 */
public interface AnnouncementRepository extends FullRepository<Integer, Announcement> {

    List<Announcement> getMostRecentFirst(String courseId);

    List<Announcement> getUnreadMostRecentFirst();

    public Map<Course, List<Announcement>> getMostRecentFirstMap();

    Map<Integer, ZonedDateTime> getIdsAndReadDateFor(Course course);
}