package be.ugent.zeus.hydra.data.database.minerva.agenda;

import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.data.database.minerva.AbstractDaoTest;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDatabaseRepository;
import be.ugent.zeus.hydra.data.database.minerva.FullRepositoryTest;
import be.ugent.zeus.hydra.data.database.minerva.mocks.MockCalendarDao;
import be.ugent.zeus.hydra.data.dto.minerva.AgendaItemDTO;
import be.ugent.zeus.hydra.data.dto.minerva.AgendaMapper;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseMapper;
import be.ugent.zeus.hydra.domain.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.domain.models.minerva.Course;
import be.ugent.zeus.hydra.domain.repository.AgendaItemRepository;
import be.ugent.zeus.hydra.domain.repository.FullRepository;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static be.ugent.zeus.hydra.utils.IterableUtils.transform;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
public class CalendarRepositoryTest extends FullRepositoryTest<Integer, AgendaItem> {

    private CourseMapper courseMapper;
    private AgendaMapper calendarMapper;
    private List<CourseDTO> courses;
    private Map<String, CourseDTO> courseMap;
    private List<AgendaItemDTO> calendarItems;
    private AgendaItemRepository agendaItemRepository;

    @Before
    public void setUp() throws IOException {
        courses = AbstractDaoTest.loadTestCourses();
        calendarItems = AbstractDaoTest.loadTestCalendarItems();
        courseMapper = CourseMapper.INSTANCE;
        calendarMapper = AgendaMapper.INSTANCE;
        agendaItemRepository = new AgendaDatabaseRepository(
                new MockCalendarDao(calendarItems, courses),
                courseMapper,
                calendarMapper);
        courseMap = new HashMap<>();
        for (CourseDTO c: courses) {
            this.courseMap.put(c.getId(), c);
        }
    }

    @Override
    protected FullRepository<Integer, AgendaItem> getRepository() {
        return agendaItemRepository;
    }

    @Override
    protected List<AgendaItem> getData() {
        return transform(calendarItems, a -> calendarMapper.convert(a, courseMapper.courseToCourse(courseMap.get(a.getCourseId()))));
    }

    @Override
    protected Function<AgendaItem, Integer> getIdExtractor() {
        return AgendaItem::getItemId;
    }

    @Override
    protected void assertDeepEquals(AgendaItem expected, AgendaItem actual) {
        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Override
    protected AgendaItem generateRandom() {
        Course random = courseMapper.courseToCourse(getRandom(courses));
        AgendaItem agendaItem = generate(AgendaItem.class, "course", "endDate");
        agendaItem.setCourse(random);
        agendaItem.setEndDate(agendaItem.getStartDate().plusHours(3));
        return agendaItem;
    }

    @Override
    protected List<AgendaItem> generateRandom(int amount) {
        List<Course> randoms = transform(getRandom(courses, amount), courseDTO -> courseMapper.courseToCourse(courseDTO));
        List<AgendaItem> agendaItems = generate(AgendaItem.class, amount, "course", "endDate").collect(Collectors.toList());
        for (int i = 0; i < amount; i++) {
            agendaItems.get(i).setCourse(randoms.get(i));
            agendaItems.get(i).setEndDate(agendaItems.get(i).getStartDate().plusHours(3));
        }
        return agendaItems;
    }

    @Override
    protected AgendaItem generateRandomUpdateFor(AgendaItem other) {
        AgendaItem random = generate(AgendaItem.class, "itemId", "course", "endDate");
        random.setItemId(other.getItemId());
        random.setCourse(other.getCourse());
        random.setEndDate(random.getStartDate().plusHours(3));
        return random;
    }

    @Override
    protected List<AgendaItem> generateRandomUpdateFor(List<AgendaItem> other) {
        List<AgendaItem> agendaItems = generate(AgendaItem.class, other.size(),"itemId", "course", "endDate").collect(Collectors.toList());
        for (int i = 0; i < other.size(); i++) {
            agendaItems.get(i).setItemId(other.get(i).getItemId());
            agendaItems.get(i).setCourse(other.get(i).getCourse());
            agendaItems.get(i).setEndDate(agendaItems.get(i).getStartDate().plusHours(3));
        }
        return agendaItems;
    }

    @Test
    public void getAllForCourseFuture() {
        // We want a date that is possible in range.
        OffsetDateTime now = getRandom(calendarItems).getStartDate();
        Course course = courseMapper.courseToCourse(getRandom(courses));
        List<AgendaItem> expected = getData().stream()
                .filter(i -> i.getCourse().equals(course))
                .filter(i -> i.getEndDate().isAfter(now) || i.getEndDate().isEqual(now))
                .collect(Collectors.toList());
        List<AgendaItem> actual = agendaItemRepository.getAllForCourseFuture(course.getId(), now);
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getBetween() {
        OffsetDateTime lower = getRandom(calendarItems).getStartDate();
        OffsetDateTime upper = lower.plusWeeks(1);

        List<AgendaItem> expected = getData().stream()
                .filter(c -> {
                    boolean startAfter = c.getStartDate().isAfter(lower) || c.getStartDate().isEqual(lower);
                    boolean endAfter = c.getEndDate().isAfter(lower) || c.getEndDate().isEqual(lower);
                    boolean startUpper = c.getStartDate().isBefore(upper) || c.getStartDate().isEqual(upper);
                    return (startAfter || endAfter) && startUpper;
                })
                .collect(Collectors.toList());
        List<AgendaItem> actual = agendaItemRepository.getBetween(lower, upper);
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getAllWithCalendarId() {
        Map<Integer, Long> expected = new HashMap<>();
        for (AgendaItem course: getData()) {
            expected.put(course.getItemId(), course.getCalendarId());
        }
        Map<Integer, Long> actual = agendaItemRepository.getIdsAndCalendarIds();
        assertEquals(expected, actual);
    }

    @Test
    public void getCalendarIdsForIds() {
        final int NR_OF_ITEMS = 5;
        Set<Integer> ids = getRandom(calendarItems, NR_OF_ITEMS).stream()
                .map(AgendaItemDTO::getId)
                .collect(Collectors.toSet());

        List<Long> expected = calendarItems.stream()
                .filter(i -> ids.contains(i.getId()))
                .map(AgendaItemDTO::getCalendarId)
                .collect(Collectors.toList());
        List<Long> actual = agendaItemRepository.getCalendarIdsForIds(ids);
        assertEquals(expected, actual);
    }
}