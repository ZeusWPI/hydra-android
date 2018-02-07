package be.ugent.zeus.hydra.data.database.minerva.course;

import android.support.annotation.RequiresApi;
import android.util.Pair;

import be.ugent.zeus.hydra.data.database.minerva.AbstractDaoTest;
import be.ugent.zeus.hydra.data.database.minerva.CourseDatabaseRepository;
import be.ugent.zeus.hydra.data.database.minerva.FullRepositoryTest;
import be.ugent.zeus.hydra.data.database.minerva.mocks.MockCourseDao;
import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseMapper;
import be.ugent.zeus.hydra.domain.models.minerva.Course;
import be.ugent.zeus.hydra.domain.repository.CourseRepository;
import be.ugent.zeus.hydra.domain.repository.FullRepository;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.assertCollectionEquals;
import static be.ugent.zeus.hydra.testing.Assert.assertThat;
import static be.ugent.zeus.hydra.testing.Assert.samePropertyValuesAs;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static be.ugent.zeus.hydra.utils.IterableUtils.transform;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
public class CourseRepositoryTest extends FullRepositoryTest<String, Course> {

    private CourseMapper courseMapper;
    private List<CourseDTO> courses;
    private List<AnnouncementDTO> announcements;
    private CourseRepository courseRepository;

    @Before
    public void setUp() throws IOException {
        courses = AbstractDaoTest.loadTestCourses();
        announcements = AbstractDaoTest.loadTestAnnouncements();
        courseMapper = CourseMapper.INSTANCE;
        courseRepository = new CourseDatabaseRepository(new MockCourseDao(courses, announcements), courseMapper);
    }

    @Override
    protected FullRepository<String, Course> getRepository() {
        return courseRepository;
    }

    @Override
    protected List<Course> getData() {
        return transform(courses, courseDTO -> courseMapper.courseToCourse(courseDTO));
    }

    @Override
    protected Function<Course, String> getIdExtractor() {
        return Course::getId;
    }

    @Override
    protected void assertDeepEquals(Course expected, Course actual) {
        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Override
    protected Course generateRandom() {
        return generate(Course.class);
    }

    @Override
    protected List<Course> generateRandom(int amount) {
        return generate(Course.class, amount).collect(Collectors.toList());
    }

    @Override
    protected Course generateRandomUpdateFor(Course other) {
        Course newCourse = generateRandom();
        newCourse.setId(other.getId());
        return newCourse;
    }

    @Override
    protected List<Course> generateRandomUpdateFor(List<Course> other) {
        return other.stream()
                .map(this::generateRandomUpdateFor)
                .collect(Collectors.toList());
    }

    @Test
    public void getIn() {
        final int NR_OF_ITEMS = 5;
        List<Course> randomCourses = getRandom(getData(), NR_OF_ITEMS);
        List<Course> actual = courseRepository.getIn(randomCourses.stream().map(Course::getId).collect(Collectors.toList()));
        assertCollectionEquals(randomCourses, actual);
    }

    @Test
    public void getAllAndUnreadInOrder() {
        List<Pair<Course, Long>> expected = getData().stream()
                .sorted(Comparator.comparing(Course::getOrder).thenComparing(Course::getTitle))
                .map(course -> new Pair<>(course, announcements.stream().filter(a -> a.getCourseId().equals(course.getId())).count()))
                .collect(Collectors.toList());
        List<Pair<Course, Long>> actual = courseRepository.getAllAndUnreadInOrder();
        assertEquals(expected, actual);
    }

    @Test
    public void getIdToOrderMap() {
        Map<String, CourseRepository.LocalData> expected = new HashMap<>();
        for (Course course: getData()) {
            expected.put(course.getId(), new CourseRepository.LocalData(course.getOrder(), course.getDisabledModules()));
        }
        Map<String, CourseRepository.LocalData> actual = courseRepository.getIdToLocalData();
        assertEquals(expected, actual);
    }

    @Test
    public void getIds() {
        List<String> expected = getData().stream().map(Course::getId).collect(Collectors.toList());
        List<String> actual = courseRepository.getIds();
        assertCollectionEquals(expected, actual);
    }
}
