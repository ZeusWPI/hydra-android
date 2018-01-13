package be.ugent.zeus.hydra.minerva.database.announcement;

import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.common.FullRepository;
import be.ugent.zeus.hydra.common.FullRepositoryTest;
import be.ugent.zeus.hydra.minerva.Announcement;
import be.ugent.zeus.hydra.minerva.AnnouncementRepository;
import be.ugent.zeus.hydra.minerva.Course;
import be.ugent.zeus.hydra.minerva.database.AbstractDaoTest;
import be.ugent.zeus.hydra.minerva.database.AnnouncementDatabaseRepository;
import be.ugent.zeus.hydra.minerva.database.MockAnnouncementDao;
import be.ugent.zeus.hydra.minerva.dto.AnnouncementDTO;
import be.ugent.zeus.hydra.minerva.dto.AnnouncementMapper;
import be.ugent.zeus.hydra.minerva.dto.CourseDTO;
import be.ugent.zeus.hydra.minerva.dto.CourseMapper;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static be.ugent.zeus.hydra.utils.IterableUtils.transform;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
public class AnnouncementRepositoryTest extends FullRepositoryTest<Integer, Announcement> {

    private CourseMapper courseMapper;
    private AnnouncementMapper announcementMapper;
    private List<CourseDTO> courses;
    private Map<String, CourseDTO> courseMap;
    private List<AnnouncementDTO> announcements;
    private AnnouncementRepository announcementRepository;

    @Before
    public void setUp() throws IOException {
        courses = AbstractDaoTest.loadTestCourses();
        announcements = AbstractDaoTest.loadTestAnnouncements();
        courseMapper = CourseMapper.INSTANCE;
        announcementMapper = AnnouncementMapper.INSTANCE;
        announcementRepository = new AnnouncementDatabaseRepository(
                new MockAnnouncementDao(announcements, courses),
                courseMapper,
                announcementMapper);
        courseMap = new HashMap<>();
        for (CourseDTO c: courses) {
            this.courseMap.put(c.getId(), c);
        }
    }

    @Override
    protected FullRepository<Integer, Announcement> getRepository() {
        return announcementRepository;
    }

    @Override
    protected List<Announcement> getData() {
        return transform(announcements, a -> announcementMapper.convert(a, courseMapper.courseToCourse(courseMap.get(a.getCourseId()))));
    }

    @Override
    protected Function<Announcement, Integer> getIdExtractor() {
        return Announcement::getItemId;
    }

    @Override
    protected void assertDeepEquals(Announcement expected, Announcement actual) {
        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Override
    protected Announcement generateRandom() {
        Course random = courseMapper.courseToCourse(getRandom(courses));
        Announcement announcement = generate(Announcement.class, "course");
        announcement.setCourse(random);
        return announcement;
    }

    @Override
    protected List<Announcement> generateRandom(int amount) {
        List<Course> randoms = transform(getRandom(courses, amount), courseDTO -> courseMapper.courseToCourse(courseDTO));
        List<Announcement> announcements = generate(Announcement.class, amount, "course").collect(Collectors.toList());
        for (int i = 0; i < amount; i++) {
            announcements.get(i).setCourse(randoms.get(i));
        }
        return announcements;
    }

    @Override
    protected Announcement generateRandomUpdateFor(Announcement other) {
        Announcement random = generate(Announcement.class, "itemId", "course");
        random.setItemId(other.getItemId());
        random.setCourse(other.getCourse());
        return random;
    }

    @Override
    protected List<Announcement> generateRandomUpdateFor(List<Announcement> other) {
        List<Announcement> randoms = generate(Announcement.class, other.size(), "itemId", "course").collect(Collectors.toList());
        for (int i = 0; i < other.size(); i++) {
            randoms.get(i).setItemId(other.get(i).getItemId());
            randoms.get(i).setCourse(other.get(i).getCourse());
        }
        return randoms;
    }

    @Test
    public void getMostRecentFirst() {
        Course random = courseMapper.courseToCourse(getRandom(courses));
        List<Announcement> expected = getData().stream()
                .filter(a -> a.getCourse().equals(random))
                .sorted(Comparator.comparing(Announcement::getDate).reversed())
                .collect(Collectors.toList());
        List<Announcement> actual = announcementRepository.getMostRecentFirst(random.getId());
        assertEquals(expected, actual);
    }

    @Test
    public void getUnreadMostRecentFirst() {
        List<Announcement> expected = getData().stream()
                .filter(((Predicate<Announcement>) Announcement::isRead).negate())
                .sorted(Comparator.comparing(Announcement::getDate).reversed())
                .collect(Collectors.toList());
        List<Announcement> actual = announcementRepository.getUnreadMostRecentFirst();
        assertEquals(expected, actual);
    }

    @Test
    public void getIdsAndReadDateFor() {
        Course random = courseMapper.courseToCourse(getRandom(courses));
        Map<Integer, Instant> expected = new HashMap<>();
        List<Announcement> data = getData().stream()
                .filter(a -> a.getCourse().equals(random))
                .collect(Collectors.toList());
        for (Announcement a: data) {
            expected.put(a.getItemId(), a.getRead());
        }
        Map<Integer, Instant> actual = announcementRepository.getIdsAndReadDateFor(random);
        assertEquals(expected, actual);
    }

    @Test
    public void getMostRecentFirstMap() {
        Map<Course, List<Announcement>> expected = getData().stream()
                .filter(a -> !a.isRead())
                .collect(Collectors.groupingBy(Announcement::getCourse));
        Map<Course, List<Announcement>> actual = announcementRepository.getMostRecentFirstMap();

        for (Course course: transform(courses, courseDTO -> courseMapper.courseToCourse(courseDTO))) {
            List<Announcement> ex = expected.get(course);
            List<Announcement> ac = actual.get(course);
            assertCollectionEquals(ex, ac);
            assertFalse(ac.isEmpty());
        }
    }
}
