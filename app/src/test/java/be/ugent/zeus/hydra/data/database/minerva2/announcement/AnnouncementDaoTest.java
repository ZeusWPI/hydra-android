package be.ugent.zeus.hydra.data.database.minerva2.announcement;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.data.database.minerva2.AbstractDaoTest;
import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.util.Pair;
import org.threeten.bp.ZonedDateTime;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.assertCollectionEquals;
import static be.ugent.zeus.hydra.testing.Assert.samePropertyValuesAs;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
public class AnnouncementDaoTest extends AbstractDaoTest {

    private AnnouncementDao announcementDao;

    @Before
    public void getDao() {
        this.announcementDao = database.getAnnouncementDao();
    }

    @Test
    public void getOne() throws Exception {
        // Get 5 random items from the list.
        List<AnnouncementDTO> expected = getRandom(announcements, 5);

        for (AnnouncementDTO item : expected) {
            AnnouncementDao.Result result = announcementDao.getOne(item.getId());
            assertEquals(item, result.announcement);
            assertThat(result.announcement, samePropertyValuesAs(item));
            assertEquals(courseMap.get(item.getCourseId()), result.course);
            assertThat(result.course, samePropertyValuesAs(courseMap.get(result.announcement.getCourseId())));
        }
    }

    @Test
    public void getNonExisting() {
        AnnouncementDao.Result result = announcementDao.getOne(-1);
        assertNull(result);
    }

    @Test
    public void getAll() throws Exception {
        List<AnnouncementDao.Result> results = announcementDao.getAll();
        List<AnnouncementDTO> items = results.stream()
                .map(r -> r.announcement)
                .collect(Collectors.toList());
        assertCollectionEquals(this.announcements, items);

        // Do deep equals.
        Map<Integer, AnnouncementDTO> expected = new HashMap<>();
        for (AnnouncementDTO itemDTO : this.announcements) {
            expected.put(itemDTO.getId(), itemDTO);
        }

        for (AnnouncementDao.Result actual : results) {
            assertThat(actual.announcement, samePropertyValuesAs(expected.get(actual.announcement.getId())));
            assertEquals(courseMap.get(actual.announcement.getCourseId()), actual.course);
            assertThat(actual.course, samePropertyValuesAs(courseMap.get(actual.announcement.getCourseId())));
        }
    }

    @Test
    public void insertOne() throws Exception {
        CourseDTO randomCourse = getRandom(this.courses);
        AnnouncementDTO randomItem = generate(AnnouncementDTO.class, "courseId");
        randomItem.setCourseId(randomCourse.getId());

        announcementDao.insert(randomItem);

        AnnouncementDao.Result result = announcementDao.getOne(randomItem.getId());
        assertEquals(randomItem, result.announcement);
        assertEquals(randomCourse, result.course);

        assertThat(result.announcement, samePropertyValuesAs(randomItem));
        assertThat(result.course, samePropertyValuesAs(randomCourse));
    }

    @Test
    public void insertCollection() throws Exception {
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> randomCourses = getRandom(this.courses, NR_OF_ITEMS);

        List<AnnouncementDTO> randomItems = generate(AnnouncementDTO.class, NR_OF_ITEMS, "courseId").collect(Collectors.toList());
        for (int i = 0; i < randomItems.size(); i++) {
            randomItems.get(i).setCourseId(randomCourses.get(i).getId());
        }

        announcementDao.insert(randomItems);

        for (int i = 0; i < randomItems.size(); i++) {
            AnnouncementDTO randomItem = randomItems.get(i);
            CourseDTO randomCourse = randomCourses.get(i);
            AnnouncementDao.Result result = announcementDao.getOne(randomItem.getId());
            assertEquals(randomItem, result.announcement);
            assertEquals(randomCourse, result.course);

            assertThat(result.announcement, samePropertyValuesAs(randomItem));
            assertThat(result.course, samePropertyValuesAs(randomCourse));
        }
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertExisting() {
        announcementDao.insert(getRandom(this.announcements));
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertNonExistingCourse() {
        AnnouncementDTO item = generate(AnnouncementDTO.class, "courseId", "id");
        item.setCourseId("NON EXISTING COURSE ID");
        item.setId(-1); // Can never be an existing id.
        announcementDao.insert(item);
    }

    @Test
    public void updateOne() throws Exception {
        CourseDTO newCourse = getRandom(this.courses);
        AnnouncementDTO originalItem = getRandom(this.announcements);
        AnnouncementDTO updatedItem = generate(AnnouncementDTO.class, "courseId", "id");
        // Make sure the data is correct.
        updatedItem.setId(originalItem.getId());
        updatedItem.setCourseId(newCourse.getId());

        announcementDao.update(updatedItem);

        AnnouncementDao.Result result = announcementDao.getOne(updatedItem.getId());
        assertEquals(updatedItem, result.announcement);
        assertEquals(newCourse, result.course);

        assertThat(result.announcement, samePropertyValuesAs(updatedItem));
        assertThat(result.course, samePropertyValuesAs(newCourse));
    }

    @Test
    public void updateCollection() throws Exception {
        final int NR_OF_ITEMS = 5;
        List<CourseDTO> newCourses = getRandom(this.courses, NR_OF_ITEMS);
        List<AnnouncementDTO> originalItems = getRandom(this.announcements, NR_OF_ITEMS);
        List<AnnouncementDTO> updatedItems = generate(AnnouncementDTO.class, NR_OF_ITEMS, "courseId", "id").collect(Collectors.toList());
        // Make sure the data is correct.
        for (int i = 0; i < NR_OF_ITEMS; i++) {
            updatedItems.get(i).setId(originalItems.get(i).getId());
            updatedItems.get(i).setCourseId(newCourses.get(i).getId());
        }

        announcementDao.update(updatedItems);

        for (int i = 0; i < NR_OF_ITEMS; i++) {
            AnnouncementDao.Result result = announcementDao.getOne(updatedItems.get(i).getId());
            assertEquals(updatedItems.get(i), result.announcement);
            assertEquals(newCourses.get(i), result.course);

            assertThat(result.announcement, samePropertyValuesAs(updatedItems.get(i)));
            assertThat(result.course, samePropertyValuesAs(newCourses.get(i)));
        }
    }

    @Test
    public void deleteOne() throws Exception {
        AnnouncementDTO original = getRandom(this.announcements);
        announcementDao.delete(original);
        List<AnnouncementDTO> items = announcementDao.getAll().stream()
                .map(result -> result.announcement)
                .collect(Collectors.toList());
        assertEquals(this.calendarItems.size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test
    public void deleteMultiple() throws Exception {
        int NR_OF_ITEMS = 5;
        List<AnnouncementDTO> originals = getRandom(this.announcements, NR_OF_ITEMS);
        announcementDao.delete(originals);
        List<AnnouncementDTO> items = announcementDao.getAll().stream().map(result -> result.announcement).collect(Collectors.toList());
        assertEquals(this.calendarItems.size() - NR_OF_ITEMS, items.size());
        for (AnnouncementDTO original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test
    public void deleteAll() throws Exception {
        announcementDao.deleteAll();
        assertTrue(announcementDao.getAll().isEmpty());
    }

    @Test
    public void deleteById() throws Exception {
        int NR_OF_ITEMS = 5;
        List<AnnouncementDTO> originals = getRandom(this.announcements, NR_OF_ITEMS);
        announcementDao.deleteById(originals.stream().map(AnnouncementDTO::getId).collect(Collectors.toList()));
        List<AnnouncementDTO> items = announcementDao.getAll().stream().map(result -> result.announcement).collect(Collectors.toList());
        assertEquals(this.calendarItems.size() - NR_OF_ITEMS, items.size());
        for (AnnouncementDTO original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test
    public void deleteOneById() throws Exception {
        AnnouncementDTO original = getRandom(this.announcements);
        announcementDao.delete(original.getId());
        List<AnnouncementDTO> items = announcementDao.getAll().stream().map(result -> result.announcement).collect(Collectors.toList());
        assertEquals(this.calendarItems.size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test
    public void getUnreadMostRecentFirst() throws Exception {
        List<AnnouncementDTO> expected = this.announcements.stream()
                .filter(announcementDTO -> announcementDTO.getReadAt() == null)
                .sorted(Comparator.comparing(AnnouncementDTO::getLastEditedAt))
                .collect(Collectors.toList());
        List<AnnouncementDTO> actual = announcementDao.getUnreadMostRecentFirst().stream()
                .map(r -> r.announcement)
                .collect(Collectors.toList());
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getMostRecentFirst() throws Exception {
        CourseDTO randomCourse = getRandom(this.courses);
        List<AnnouncementDTO> expected = this.announcements.stream()
                .filter(announcementDTO -> announcementDTO.getCourseId().equals(randomCourse.getId()))
                .sorted(Comparator.comparing(AnnouncementDTO::getLastEditedAt))
                .collect(Collectors.toList());
        List<AnnouncementDTO> actual = this.announcementDao.getMostRecentFirst(randomCourse.getId()).stream()
                .map(r -> r.announcement)
                .collect(Collectors.toList());
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void getIdAndReadDateFor() throws Exception {
        CourseDTO randomCourse = getRandom(this.courses);
        List<Pair<Integer, ZonedDateTime>> expected = this.announcements.stream()
                .filter(a -> a.getCourseId().equals(randomCourse.getId()))
                .map(announcementDTO -> new Pair<>(announcementDTO.getId(), announcementDTO.getReadAt()))
                .collect(Collectors.toList());
        List<Pair<Integer, ZonedDateTime>> actual = announcementDao.getIdAndReadDateFor(randomCourse.getId()).stream()
                .map(idAndReadDate -> new Pair<>(idAndReadDate.id, idAndReadDate.readAt))
                .collect(Collectors.toList());
        assertCollectionEquals(expected, actual);
    }
}