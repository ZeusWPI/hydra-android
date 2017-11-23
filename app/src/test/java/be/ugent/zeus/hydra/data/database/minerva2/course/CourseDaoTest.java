package be.ugent.zeus.hydra.data.database.minerva2.course;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.data.database.minerva2.AbstractDaoTest;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.util.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.*;
import static be.ugent.zeus.hydra.testing.Assert.assertThat;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
public class CourseDaoTest extends AbstractDaoTest {

    private CourseDao dao;

    @Before
    public void getDao() {
        this.dao = database.getCourseDao();
    }

    @Test
    public void getOne() {
        List<CourseDTO> expectedCourses = getRandom(this.courses, 5);

        for (CourseDTO expected : expectedCourses) {
            CourseDTO actual = dao.getOne(expected.getId());
            assertEquals(expected, actual);
            assertThat(actual, samePropertyValuesAs(expected));
        }
    }

    @Test
    public void getNonExisting() {
        assertNull(dao.getOne("DOES NOT EXIST"));
    }

    @Test
    public void getAll() {
        List<CourseDTO> items = dao.getAll();
        assertCollectionEquals(this.courses, items);

        for (CourseDTO actual : items) {
            assertThat(actual, samePropertyValuesAs(courseMap.get(actual.getId())));
        }
    }

    @Test
    public void insertOne() {
        CourseDTO randomItem = generate(CourseDTO.class);
        dao.insert(randomItem);

        CourseDTO result = dao.getOne(randomItem.getId());
        assertEquals(randomItem, result);
        assertThat(result, samePropertyValuesAs(randomItem));
    }

    @Test
    public void insertCollection() {
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> randomItems = generate(CourseDTO.class, NR_OF_ITEMS).collect(Collectors.toList());
        dao.insert(randomItems);

        for (CourseDTO expected : randomItems) {
            CourseDTO actual = dao.getOne(expected.getId());
            assertEquals(expected, actual);
            assertThat(actual, samePropertyValuesAs(expected));
        }
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertExisting() {
        dao.insert(getRandom(this.courses));
    }

    @Test
    public void updateOne() {
        CourseDTO originalItem = getRandom(this.courses);
        CourseDTO updatedItem = generate(CourseDTO.class,"id");
        // Make sure the data is correct.
        updatedItem.setId(originalItem.getId());

        dao.update(updatedItem);

        CourseDTO actual = dao.getOne(updatedItem.getId());
        assertEquals(updatedItem, actual);
        assertThat(actual, samePropertyValuesAs(updatedItem));
    }

    @Test
    public void updateCollection() {
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> originalItems = getRandom(courses, NR_OF_ITEMS);
        List<CourseDTO> updatedItems = generate(CourseDTO.class, NR_OF_ITEMS, "id").collect(Collectors.toList());
        // Make sure the data is correct.
        for (int i = 0; i < NR_OF_ITEMS; i++) {
            updatedItems.get(i).setId(originalItems.get(i).getId());
        }

        dao.update(updatedItems);

        for (int i = 0; i < NR_OF_ITEMS; i++) {
            CourseDTO actual = dao.getOne(updatedItems.get(i).getId());
            assertEquals(updatedItems.get(i), actual);
            assertThat(actual, samePropertyValuesAs(updatedItems.get(i)));
        }
    }

    @Test(expected = SQLiteConstraintException.class)
    public void deleteOneConstraints() {
        dao.delete(getRandom(courses));
    }

    @Test
    public void deleteOne() {
        // First, clear the all other data to prevent foreign key checks from failing.
        database.getAnnouncementDao().deleteAll();
        database.getAgendaDao().deleteAll();
        CourseDTO original = getRandom(courses);
        dao.delete(original);
        List<CourseDTO> items = dao.getAll();
        assertEquals(courses.size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test(expected = SQLiteConstraintException.class)
    public void deleteCollectionConstraint() {
        dao.delete(getRandom(courses, 5));
    }

    @Test
    public void deleteCollection() {
        // First, clear the all other data to prevent foreign key checks from failing.
        database.getAnnouncementDao().deleteAll();
        database.getAgendaDao().deleteAll();
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> originals = getRandom(courses, NR_OF_ITEMS);
        dao.delete(originals);
        List<CourseDTO> items = dao.getAll();
        assertEquals(courses.size() - NR_OF_ITEMS, items.size());
        for (CourseDTO original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test(expected = SQLiteConstraintException.class)
    public void deleteAllConstraint() {
        dao.deleteAll();
    }

    @Test
    public void deleteAll() {
        // First, clear the all other data to prevent foreign key checks from failing.
        database.getAnnouncementDao().deleteAll();
        database.getAgendaDao().deleteAll();
        dao.deleteAll();
        assertTrue(dao.getAll().isEmpty());
    }

    @Test(expected = SQLiteConstraintException.class)
    public void deleteByIdConstraint() {
        dao.deleteById(getRandom(courses, 5).stream().map(CourseDTO::getId).collect(Collectors.toList()));
    }

    @Test
    public void deleteById() {
        // First, clear the all other data to prevent foreign key checks from failing.
        database.getAnnouncementDao().deleteAll();
        database.getAgendaDao().deleteAll();
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> originals = getRandom(courses, NR_OF_ITEMS);
        dao.deleteById(originals.stream().map(CourseDTO::getId).collect(Collectors.toList()));
        List<CourseDTO> items = dao.getAll();
        assertEquals(courses.size() - NR_OF_ITEMS, items.size());
        for (CourseDTO original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test(expected = SQLiteConstraintException.class)
    public void deleteOneByIdConstraint() {
        dao.delete(getRandom(courses).getId());
    }

    @Test
    public void deleteOneById() {
        // First, clear the all other data to prevent foreign key checks from failing.
        database.getAnnouncementDao().deleteAll();
        database.getAgendaDao().deleteAll();
        CourseDTO original = getRandom(courses);
        dao.delete(original.getId());
        List<CourseDTO> items = dao.getAll();
        assertEquals(courses.size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test
    public void getIn() {
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> expected = getRandom(courses, NR_OF_ITEMS);
        List<String> ids = expected.stream().map(CourseDTO::getId).collect(Collectors.toList());
        List<CourseDTO> actual = dao.getIn(ids);
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getAllAndUnreadInOrder() {
        List<Pair<CourseDTO, Long>> expected = courses.stream()
                .sorted(Comparator.comparing(CourseDTO::getOrder).thenComparing(CourseDTO::getTitle))
                .map(c -> new Pair<>(c, announcements.stream().filter(a -> a.getCourseId().equals(c.getId())).count()))
                .collect(Collectors.toList());

        List<Pair<CourseDTO, Long>> actual = dao.getAllAndUnreadInOrder().stream()
                .map(r -> new Pair<>(r.getCourse(), r.getUnreadAnnouncements()))
                .collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    @Test
    public void getIds() {
        List<String> expected = courses.stream().map(CourseDTO::getId).collect(Collectors.toList());
        List<String> actual = dao.getIds();
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getIdsAndOrders() {
        List<Pair<String, Integer>> expected = courses.stream()
                .map(c -> new Pair<>(c.getId(), c.getOrder()))
                .collect(Collectors.toList());
        List<Pair<String, Integer>> actual = dao.getIdsAndOrders().stream()
                .map(r -> new Pair<>(r.id, r.order))
                .collect(Collectors.toList());
        assertCollectionEquals(expected, actual);
    }
}