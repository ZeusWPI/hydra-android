package be.ugent.zeus.hydra.data.database.minerva2.agenda;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.data.database.minerva2.AbstractDaoTest;
import be.ugent.zeus.hydra.data.dto.minerva.AgendaItemDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.ZonedDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
public class AgendaDaoTest extends AbstractDaoTest {

    private AgendaDao dao;

    @Before
    public void getDao() {
        this.dao = database.getAgendaDao();
    }

    @Test
    public void getOne() {
        // Get 5 random items from the list.
        List<AgendaItemDTO> expected = getRandom(this.calendarItems, 5);

        for (AgendaItemDTO item : expected) {
            AgendaDao.Result result = dao.getOne(item.getId());
            assertEquals(item, result.agendaItem);
            assertThat(result.agendaItem, samePropertyValuesAs(item));
            assertEquals(courseMap.get(item.getCourseId()), result.course);
            assertThat(result.course, samePropertyValuesAs(courseMap.get(result.agendaItem.getCourseId())));
        }
    }

    @Test
    public void getNonExisting() {
        AgendaDao.Result result = dao.getOne(-1);
        assertNull(result);
    }

    @Test
    public void getAll() {
        List<AgendaDao.Result> results = dao.getAll();
        List<AgendaItemDTO> items = results.stream()
                .map(r -> r.agendaItem)
                .collect(Collectors.toList());
        assertCollectionEquals(this.calendarItems, items);

        @SuppressLint("UseSparseArrays")
        Map<Integer, AgendaItemDTO> expected = new HashMap<>();
        for (AgendaItemDTO itemDTO : this.calendarItems) {
            expected.put(itemDTO.getId(), itemDTO);
        }

        for (AgendaDao.Result actual : results) {
            assertThat(actual.agendaItem, samePropertyValuesAs(expected.get(actual.agendaItem.getId())));
            assertEquals(courseMap.get(actual.agendaItem.getCourseId()), actual.course);
            assertThat(actual.course, samePropertyValuesAs(courseMap.get(actual.agendaItem.getCourseId())));
        }
    }

    @Test
    public void insertOne() {
        CourseDTO randomCourse = getRandom(this.courses);
        AgendaItemDTO randomItem = generate(AgendaItemDTO.class, "courseId", "endDate");
        randomItem.setCourseId(randomCourse.getId());
        randomItem.setEndDate(randomItem.getStartDate().plusHours(2));

        dao.insert(randomItem);

        AgendaDao.Result result = dao.getOne(randomItem.getId());
        assertEquals(randomItem, result.agendaItem);
        assertEquals(randomCourse, result.course);

        assertThat(result.agendaItem, samePropertyValuesAs(randomItem));
        assertThat(result.course, samePropertyValuesAs(randomCourse));
    }

    @Test
    public void insertCollection() {
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> randomCourses = getRandom(this.courses, NR_OF_ITEMS);

        List<AgendaItemDTO> randomItems = generate(AgendaItemDTO.class, NR_OF_ITEMS, "courseId", "endDate").collect(Collectors.toList());
        for (int i = 0; i < randomItems.size(); i++) {
            randomItems.get(i).setCourseId(randomCourses.get(i).getId());
            randomItems.get(i).setEndDate(randomItems.get(i).getStartDate().plusHours(i + 1));
        }

        dao.insert(randomItems);

        for (int i = 0; i < randomItems.size(); i++) {
            AgendaItemDTO randomItem = randomItems.get(i);
            CourseDTO randomCourse = randomCourses.get(i);
            AgendaDao.Result result = dao.getOne(randomItem.getId());
            assertEquals(randomItem, result.agendaItem);
            assertEquals(randomCourse, result.course);

            assertThat(result.agendaItem, samePropertyValuesAs(randomItem));
            assertThat(result.course, samePropertyValuesAs(randomCourse));
        }
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertExisting() {
        dao.insert(getRandom(this.calendarItems));
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertNonExistingCourse() {
        AgendaItemDTO item = generate(AgendaItemDTO.class, "courseId", "endDate", "id");
        item.setCourseId("NON EXISTING COURSE ID");
        item.setId(-1); // Can never be an existing id.
        item.setEndDate(item.getStartDate().plusHours(1));
        dao.insert(item);
    }

    @Test
    public void updateOne() {
        CourseDTO newCourse = getRandom(this.courses);
        AgendaItemDTO originalItem = getRandom(this.calendarItems);
        AgendaItemDTO updatedItem = generate(AgendaItemDTO.class, "courseId", "endDate", "id");
        // Make sure the data is correct.
        updatedItem.setId(originalItem.getId());
        updatedItem.setCourseId(newCourse.getId());
        updatedItem.setEndDate(updatedItem.getStartDate().plusHours(5));

        dao.update(updatedItem);

        AgendaDao.Result result = dao.getOne(updatedItem.getId());
        assertEquals(updatedItem, result.agendaItem);
        assertEquals(newCourse, result.course);

        assertThat(result.agendaItem, samePropertyValuesAs(updatedItem));
        assertThat(result.course, samePropertyValuesAs(newCourse));
    }

    @Test
    public void updateCollection() {
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> newCourses = getRandom(this.courses, NR_OF_ITEMS);
        List<AgendaItemDTO> originalItems = getRandom(this.calendarItems, NR_OF_ITEMS);
        List<AgendaItemDTO> updatedItems = generate(AgendaItemDTO.class, NR_OF_ITEMS, "courseId", "endDate", "id").collect(Collectors.toList());
        // Make sure the data is correct.
        for (int i = 0; i < NR_OF_ITEMS; i++) {
            updatedItems.get(i).setId(originalItems.get(i).getId());
            updatedItems.get(i).setCourseId(newCourses.get(i).getId());
            updatedItems.get(i).setEndDate(updatedItems.get(i).getStartDate().plusHours(5));
        }

        dao.update(updatedItems);

        for (int i = 0; i < NR_OF_ITEMS; i++) {
            AgendaDao.Result result = dao.getOne(updatedItems.get(i).getId());
            assertEquals(updatedItems.get(i), result.agendaItem);
            assertEquals(newCourses.get(i), result.course);

            assertThat(result.agendaItem, samePropertyValuesAs(updatedItems.get(i)));
            assertThat(result.course, samePropertyValuesAs(newCourses.get(i)));
        }
    }

    @Test
    public void deleteOne() {
        AgendaItemDTO original = getRandom(this.calendarItems);
        dao.delete(original);
        List<AgendaItemDTO> items = dao.getAll().stream().map(result -> result.agendaItem).collect(Collectors.toList());
        assertEquals(this.calendarItems.size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test
    public void deleteMultiple() {
        int NR_OF_ITEMS = 5;
        List<AgendaItemDTO> originals = getRandom(this.calendarItems, NR_OF_ITEMS);
        dao.delete(originals);
        List<AgendaItemDTO> items = dao.getAll().stream().map(result -> result.agendaItem).collect(Collectors.toList());
        assertEquals(this.calendarItems.size() - NR_OF_ITEMS, items.size());
        for (AgendaItemDTO original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test
    public void deleteAll() {
        dao.deleteAll();
        assertTrue(dao.getAll().isEmpty());
    }

    @Test
    public void deleteById() {
        int NR_OF_ITEMS = 5;
        List<AgendaItemDTO> originals = getRandom(this.calendarItems, NR_OF_ITEMS);
        dao.deleteById(originals.stream().map(AgendaItemDTO::getId).collect(Collectors.toList()));
        List<AgendaItemDTO> items = dao.getAll().stream().map(result -> result.agendaItem).collect(Collectors.toList());
        assertEquals(this.calendarItems.size() - NR_OF_ITEMS, items.size());
        for (AgendaItemDTO original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test
    public void deleteOneById() {
        AgendaItemDTO original = getRandom(this.calendarItems);
        dao.delete(original.getId());
        List<AgendaItemDTO> items = dao.getAll().stream().map(result -> result.agendaItem).collect(Collectors.toList());
        assertEquals(this.calendarItems.size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test
    public void getAllFutureForCourse() throws Exception {
        CourseDTO randomCourse = getRandom(courses);
        // We want a date that is possible in range.
        ZonedDateTime now = getRandom(calendarItems).getStartDate();
        List<AgendaItemDTO> expected = this.calendarItems.stream()
                .filter(i -> i.getCourseId().equals(randomCourse.getId()))
                .filter(i -> i.getEndDate().isAfter(now) || i.getEndDate().isEqual(now))
                .collect(Collectors.toList());
        List<AgendaItemDTO> actual = dao.getAllFutureForCourse(randomCourse.getId(), now).stream()
                .map(r -> r.agendaItem)
                .collect(Collectors.toList());
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getAllForCourse() {
        CourseDTO courseDTO = getRandom(this.courses);
        List<AgendaItemDTO> expected = this.calendarItems.stream()
                .filter(i -> i.getCourseId().equals(courseDTO.getId()))
                .collect(Collectors.toList());
        List<AgendaItemDTO> actual = dao.getAllForCourse(courseDTO.getId()).stream()
                .map(r -> r.agendaItem)
                .collect(Collectors.toList());
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getBetween() {
        ZonedDateTime lower = getRandom(this.calendarItems).getStartDate();
        ZonedDateTime higher = lower.plusWeeks(2);
        List<AgendaItemDTO> expected = this.calendarItems.stream()
                .filter(i -> {
                    boolean startDateAfterLower = i.getStartDate().isAfter(lower) || i.getStartDate().isEqual(lower);
                    boolean endDateAfterLower = i.getEndDate().isAfter(lower) || i.getEndDate().isEqual(lower);
                    boolean startDateBeforeHigher = i.getStartDate().isBefore(higher) || i.getStartDate().isEqual(higher);
                    return (startDateAfterLower || endDateAfterLower) && startDateBeforeHigher;
                })
                .collect(Collectors.toList());
        List<AgendaItemDTO> actual = dao.getBetween(lower, higher).stream()
                .map(r -> r.agendaItem)
                .collect(Collectors.toList());

        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getCalendarIdsForIds() {
        List<Integer> itemIds = this.calendarItems.stream()
                .map(AgendaItemDTO::getId)
                .collect(Collectors.toList());
        List<Long> expectedCalendarIds = this.calendarItems.stream()
                .map(AgendaItemDTO::getCalendarId)
                .collect(Collectors.toList());

        List<Long> actualCalendarIds = dao.getCalendarIdsForIds(itemIds);

        assertCollectionEquals(expectedCalendarIds, actualCalendarIds);
    }

}