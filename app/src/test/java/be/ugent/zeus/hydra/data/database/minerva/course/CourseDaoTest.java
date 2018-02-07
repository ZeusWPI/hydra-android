package be.ugent.zeus.hydra.data.database.minerva.course;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.data.database.minerva.AbstractDaoTest;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.util.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    @Test
    public void deleteOne() {
        // First, clear the all other data to prevent foreign key checks from failing.
        CourseDTO original = getRandom(courses);
        dao.delete(original);
        List<CourseDTO> items = dao.getAll();
        assertEquals(courses.size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test
    public void deleteCollection() {
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> originals = getRandom(courses, NR_OF_ITEMS);
        dao.delete(originals);
        List<CourseDTO> items = dao.getAll();
        assertEquals(courses.size() - NR_OF_ITEMS, items.size());
        for (CourseDTO original : originals) {
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
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> originals = getRandom(courses, NR_OF_ITEMS);
        dao.deleteById(originals.stream().map(CourseDTO::getId).collect(Collectors.toList()));
        List<CourseDTO> items = dao.getAll();
        assertEquals(courses.size() - NR_OF_ITEMS, items.size());
        for (CourseDTO original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test
    public void deleteOneById() {
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
        List<Triple<String, Integer, Integer>> expected = courses.stream()
                .map(c -> new Triple<>(c.getId(), c.getOrder(), c.getDisabledModules()))
                .collect(Collectors.toList());
        List<Triple<String, Integer, Integer>> actual = dao.getIdsAndLocalData().stream()
                .map(r -> new Triple<>(r.id, r.order, r.disabledModules))
                .collect(Collectors.toList());
        assertCollectionEquals(expected, actual);
    }

    private static class Triple<A, B, C> {
        private final A one;
        private final B two;
        private final C three;

        private Triple(A one, B two, C three) {
            this.one = one;
            this.two = two;
            this.three = three;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
            return Objects.equals(one, triple.one) &&
                    Objects.equals(two, triple.two) &&
                    Objects.equals(three, triple.three);
        }

        @Override
        public int hashCode() {
            return Objects.hash(one, two, three);
        }
    }
}