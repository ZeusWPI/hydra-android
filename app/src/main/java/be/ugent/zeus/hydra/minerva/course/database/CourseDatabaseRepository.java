package be.ugent.zeus.hydra.minerva.course.database;

import android.util.Pair;

import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.CourseRepository;
import be.ugent.zeus.hydra.minerva.course.Module;

import java.util.*;

import static be.ugent.zeus.hydra.utils.IterableUtils.transform;


/**
 * @author Niko Strijbol
 */
public class CourseDatabaseRepository implements CourseRepository {

    private final CourseDao courseDao;
    private final CourseMapper courseMapper;

    public CourseDatabaseRepository(CourseDao courseDao, CourseMapper courseMapper) {
        this.courseDao = courseDao;
        this.courseMapper = courseMapper;
    }

    @Override
    public Course getOne(String id) {
        return courseMapper.courseToCourse(courseDao.getOne(id));
    }

    @Override
    public List<Course> getAll() {

        return transform(courseDao.getAll(), courseMapper::courseToCourse);
    }

    @Override
    public void insert(Course object) {
        courseDao.insert(courseMapper.courseToCourse(object));
    }

    @Override
    public void insert(Collection<Course> objects) {
        courseDao.insert(transform(objects, courseMapper::courseToCourse));
    }

    @Override
    public void update(Course object) {
        courseDao.update(courseMapper.courseToCourse(object));
    }

    @Override
    public void update(Collection<Course> objects) {
        courseDao.update(transform(objects, courseMapper::courseToCourse));
    }

    @Override
    public void delete(Course object) {
        courseDao.delete(courseMapper.courseToCourse(object));
    }

    @Override
    public void deleteById(String s) {
        courseDao.delete(s);
    }

    @Override
    public void deleteById(Collection<String> id) {
        courseDao.deleteById(new ArrayList<>(id));
    }

    @Override
    public void deleteAll() {
        courseDao.deleteAll();
    }

    @Override
    public void delete(Collection<Course> objects) {
        courseDao.delete(transform(objects, courseMapper::courseToCourse));
    }

    @Override
    public List<Course> getIn(List<String> ids) {
        return transform(courseDao.getIn(ids), courseMapper::courseToCourse);
    }

    @Override
    public List<Pair<Course, Long>> getAllAndUnreadInOrder() {
        ArrayList<Pair<Course, Long>> result = new ArrayList<>();
        for (CourseUnread courseUnread : courseDao.getAllAndUnreadInOrder()) {
            result.add(new Pair<>(courseMapper.courseToCourse(courseUnread.getCourse()), courseUnread.getUnreadAnnouncements()));
        }
        return result;
    }

    @Override
    public Map<String, LocalData> getIdToLocalData() {
        Map<String, LocalData> result = new HashMap<>();
        List<CourseDao.IdAndLocalData> orders = courseDao.getIdsAndLocalData();
        for (CourseDao.IdAndLocalData idAndOrder : orders) {
            result.put(idAndOrder.id, from(idAndOrder));
        }
        return result;
    }

    @Override
    public List<String> getIds() {
        return courseDao.getIds();
    }

    private LocalData from(CourseDao.IdAndLocalData idAndLocalData) {
        return new LocalData(
                idAndLocalData.order,
                Module.fromNumericalValue(idAndLocalData.disabledModules),
                idAndLocalData.ignoreAnnouncements,
                idAndLocalData.ignoreCalendar
        );
    }
}