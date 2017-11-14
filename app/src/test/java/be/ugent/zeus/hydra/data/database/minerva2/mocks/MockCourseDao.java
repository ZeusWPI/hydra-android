package be.ugent.zeus.hydra.data.database.minerva2.mocks;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.data.database.minerva2.course.CourseDao;
import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseUnread;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock for the course dao.
 *
 * Does currently not support transaction-like behaviour of SQL, e.g. when updating an entity for which a foreign key
 * constraint fails, the item will be removed from the set and then the operation will fail.
 *
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MockCourseDao implements CourseDao {

    private final List<CourseDTO> courses;
    private final Map<String, CourseDTO> idMap;
    private final List<AnnouncementDTO> announcements;

    public MockCourseDao(List<CourseDTO> courses, List<AnnouncementDTO> announcements) {
        this.courses = new ArrayList<>(courses);
        this.announcements = new ArrayList<>(announcements);
        this.idMap = new HashMap<>();
        for (CourseDTO course: courses) {
            idMap.put(course.getId(), course);
        }
    }

    @Override
    public CourseDTO getOne(String id) {
        return idMap.get(id);
    }

    @Override
    public List<CourseDTO> getAll() {
        return Collections.unmodifiableList(courses);
    }

    @Override
    public void insert(CourseDTO course) {
        courses.add(course);
        idMap.put(course.getId(), course);
    }

    @Override
    public void insert(Collection<CourseDTO> courses) {
        courses.forEach(this::insert);
    }

    @Override
    public void update(CourseDTO course) {
        this.delete(course);
        this.insert(course);
    }

    @Override
    public void update(Collection<CourseDTO> courses) {
        courses.forEach(this::update);
    }

    @Override
    public void delete(CourseDTO course) {
        courses.remove(course);
        idMap.remove(course.getId());
    }

    @Override
    public void delete(Collection<CourseDTO> courses) {
        courses.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        courses.clear();
        idMap.clear();
    }

    @Override
    public void delete(String id) {
        courses.remove(idMap.get(id));
        idMap.remove(id);
    }

    @Override
    public void deleteById(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    public List<CourseDTO> getIn(List<String> ids) {
        return courses.stream()
                .filter(i -> ids.contains(i.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseUnread> getAllAndUnreadInOrder() {
        return courses.stream()
                .sorted(Comparator.comparing(CourseDTO::getOrder).thenComparing(CourseDTO::getTitle))
                .map(c -> {
                    CourseUnread courseUnread = new CourseUnread();
                    courseUnread.setCourse(c);
                    courseUnread.setUnreadAnnouncements(announcements.stream().filter(a -> a.getCourseId().equals(c.getId())).count());
                    return courseUnread;
                }).collect(Collectors.toList());
    }

    @Override
    public List<String> getIds() {
        return courses.stream()
                .map(CourseDTO::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<IdAndOrder> getIdsAndOrders() {
        return courses.stream()
                .map(courseDTO -> new IdAndOrder(courseDTO.getId(), courseDTO.getOrder()))
                .collect(Collectors.toList());
    }
}
