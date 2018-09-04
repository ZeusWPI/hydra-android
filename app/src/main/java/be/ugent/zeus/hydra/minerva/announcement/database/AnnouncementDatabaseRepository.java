package be.ugent.zeus.hydra.minerva.announcement.database;

import android.support.annotation.Nullable;
import android.util.Log;

import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;
import be.ugent.zeus.hydra.minerva.course.database.CourseMapper;
import com.crashlytics.android.Crashlytics;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;
import org.threeten.bp.Instant;

import java.lang.reflect.Field;
import java.util.*;

import static be.ugent.zeus.hydra.utils.IterableUtils.transform;

/**
 * @author Niko Strijbol
 */
public class AnnouncementDatabaseRepository implements AnnouncementRepository {

    private final AnnouncementDao announcementDao;
    private final CourseMapper courseMapper;
    private final AnnouncementMapper announcementMapper;

    public AnnouncementDatabaseRepository(AnnouncementDao announcementDao, CourseMapper courseMapper, AnnouncementMapper announcementMapper) {
        this.announcementDao = announcementDao;
        this.courseMapper = courseMapper;
        this.announcementMapper = announcementMapper;
    }

    @Nullable
    @Override
    public Announcement getOne(Integer integer) {
        AnnouncementDao.Result result = announcementDao.getOne(integer);
        if (result == null) {
            return null;
        }
        return announcementMapper.convert(result.announcement, courseMapper.courseToCourse(result.course));
    }

    @Override
    public List<Announcement> getAll() {
        return transform(announcementDao.getAll(), result -> announcementMapper.convert(result.announcement, courseMapper.courseToCourse(result.course)));
    }

    @Override
    public void insert(Announcement object) {
        announcementDao.insert(announcementMapper.convert(object));
    }

    @Override
    public void insert(Collection<Announcement> objects) {
        announcementDao.insert(transform(objects, announcementMapper::convert));
    }

    @Override
    public void update(Announcement object) {
        announcementDao.update(announcementMapper.convert(object));
    }

    @Override
    public void update(Collection<Announcement> objects) {
        announcementDao.update(transform(objects, announcementMapper::convert));
    }

    @Override
    public void delete(Announcement object) {
        announcementDao.delete(announcementMapper.convert(object));
    }

    @Override
    public void deleteById(Integer integer) {
        announcementDao.delete(integer);
    }

    @Override
    public void deleteById(Collection<Integer> id) {
        announcementDao.deleteById(new ArrayList<>(id));
    }

    @Override
    public void deleteAll() {
        announcementDao.deleteAll();
    }

    @Override
    public void delete(Collection<Announcement> objects) {
        announcementDao.delete(transform(objects, announcementMapper::convert));
    }

    @Override
    public List<Announcement> getMostRecentFirst(String courseId) {
        return transform(announcementDao.getMostRecentFirst(courseId), result -> announcementMapper.convert(result.announcement, courseMapper.courseToCourse(result.course)));
    }

    @Override
    public Map<Course, List<Announcement>> getMostRecentFirstMap() {
        List<AnnouncementDao.Result> results = announcementDao.getUnreadMostRecentFirst();
        Map<CourseDTO, List<AnnouncementDTO>> map = StreamSupport.stream(results)
                .filter(result -> result.course != null)
                .collect(Collectors.groupingBy(r -> r.course, Collectors.mapping(r -> r.announcement, Collectors.toList())));

        Map<Course, List<Announcement>> result = new HashMap<>();

        for (Map.Entry<CourseDTO, List<AnnouncementDTO>> entry: map.entrySet()) {
            Course course = courseMapper.courseToCourse(entry.getKey());
            result.put(course, transform(entry.getValue(), announcementDTO -> announcementMapper.convert(announcementDTO, course)));
        }

        return result;
    }

    @Override
    public List<Announcement> getUnreadMostRecentFirst() {
        // Sometimes the course is null for some reason. Perhaps this is an edge case that a course is added to Minerva
        // after the course list is synced, but before the recent announcements are synced.
        // If that happens, we ignore those announcements.
        return StreamSupport.stream(announcementDao.getUnreadMostRecentFirst())
                .filter(r -> r.course != null)
                .map(r -> announcementMapper.convert(r.announcement, courseMapper.courseToCourse(r.course)))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Instant> getIdsAndReadDateFor(Course course) {
        List<AnnouncementDao.IdAndReadDate> data = announcementDao.getIdAndReadDateFor(course.getId());
        Map<Integer, Instant> result = new HashMap<>();
        for (AnnouncementDao.IdAndReadDate idAndReadDate : data) {
            result.put(idAndReadDate.id, idAndReadDate.readAt);
        }
        return result;
    }
}