package be.ugent.zeus.hydra.minerva;

import be.ugent.zeus.hydra.common.FullRepository;
import org.threeten.bp.Instant;

import java.util.List;
import java.util.Map;

/**
 * @author Niko Strijbol
 */
public interface AnnouncementRepository extends FullRepository<Integer, Announcement> {

    List<Announcement> getMostRecentFirst(String courseId);

    List<Announcement> getUnreadMostRecentFirst();

    // TODO: rethink this approach.
    Map<Course, List<Announcement>> getMostRecentFirstMap();

    Map<Integer, Instant> getIdsAndReadDateFor(Course course);
}