package be.ugent.zeus.hydra.data.database.minerva2.announcement;

import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementDTO;
import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementMapper;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseMapper;
import be.ugent.zeus.hydra.domain.models.minerva.Announcement;
import be.ugent.zeus.hydra.domain.models.minerva.Course;
import be.ugent.zeus.hydra.domain.repository.AnnouncementRepository;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.util.*;

import static be.ugent.zeus.hydra.utils.IterableUtils.transform;

/**
 * @author Niko Strijbol
 */
public class DatabaseAnnouncementRepository implements AnnouncementRepository {

    private final AnnouncementDao announcementDao;
    private final CourseMapper courseMapper;
    private final AnnouncementMapper announcementMapper;

    public DatabaseAnnouncementRepository(AnnouncementDao announcementDao, CourseMapper courseMapper, AnnouncementMapper announcementMapper) {
        this.announcementDao = announcementDao;
        this.courseMapper = courseMapper;
        this.announcementMapper = announcementMapper;
    }

    @Override
    public Announcement getOne(Integer integer) {
        AnnouncementDao.Result result = announcementDao.getOne(integer);
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
        return transform(announcementDao.getUnreadMostRecentFirst(), result -> announcementMapper.convert(result.announcement, courseMapper.courseToCourse(result.course)));
    }

    @Override
    public Map<Integer, ZonedDateTime> getIdsAndReadDateFor(Course course) {
        List<AnnouncementDao.IdAndReadDate> data = announcementDao.getIdAndReadDateFor(course.getId());
        Map<Integer, ZonedDateTime> result = new HashMap<>();
        for (AnnouncementDao.IdAndReadDate idAndReadDate : data) {
            result.put(idAndReadDate.id, idAndReadDate.readAt);
        }
        return result;
    }
}