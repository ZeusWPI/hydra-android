package be.ugent.zeus.hydra.data.database.minerva2.agenda;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.database.minerva2.MinervaDatabase;
import be.ugent.zeus.hydra.data.dto.minerva.AgendaItemDTO;
import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import be.ugent.zeus.hydra.data.gson.ZonedThreeTenTimeStampAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.threeten.bp.ZonedDateTime;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.assertCollectionEquals;
import static be.ugent.zeus.hydra.testing.Assert.assertThat;
import static be.ugent.zeus.hydra.testing.Assert.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AgendaDaoTest {

    private MinervaDatabase database;
    private AgendaDao agendaDao;

    private List<CourseDTO> courses;
    private Map<String, CourseDTO> courseMap;
    private List<AnnouncementDTO> announcements;
    private List<AgendaItemDTO> calendarItems;

    @Before
    public void setUp() throws IOException {
        Context context = RuntimeEnvironment.application;
        database = Room.inMemoryDatabaseBuilder(context, MinervaDatabase.class)
                .allowMainThreadQueries()
                .build();
        agendaDao = database.getAgendaDao();
        fillData();
    }

    public void fillData() throws IOException {
        Resource courses = new ClassPathResource("minerva/minerva_courses.sql");
        Resource announcements = new ClassPathResource("minerva/minerva_announcements.sql");
        Resource calendar = new ClassPathResource("minerva/minerva_calendar.sql");

        List<String> courseInserts = Files.readAllLines(courses.getFile().toPath());
        List<String> announcementInserts = Files.readAllLines(announcements.getFile().toPath());
        List<String> calendarInserts = Files.readAllLines(calendar.getFile().toPath());

        Consumer<String> insert = s -> database.compileStatement(s).executeInsert();
        courseInserts.forEach(insert);
        announcementInserts.forEach(insert);
        calendarInserts.forEach(insert);

        Resource jsonCourses = new ClassPathResource("minerva/minerva_courses.json");
        Type courseType = new TypeToken<List<CourseDTO>>() {}.getType();
        Resource jsonAnnouncements = new ClassPathResource("minerva/minerva_announcements.json" );
        Type announcementType = new TypeToken<List<AnnouncementDTO>>() {}.getType();
        Resource jsonCalendar = new ClassPathResource("minerva/minerva_calendar.json" );
        Type calendarType = new TypeToken<List<AgendaItemDTO>>() {}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedThreeTenTimeStampAdapter())
                .create();

        this.courses = gson.fromJson(new JsonReader(new FileReader(jsonCourses.getFile())), courseType);
        this.announcements = gson.fromJson(new JsonReader(new FileReader(jsonAnnouncements.getFile())), announcementType);
        this.calendarItems = gson.fromJson(new JsonReader(new FileReader(jsonCalendar.getFile())), calendarType);

        assertEquals("Error during data loading.", courseInserts.size(), this.courses.size());
        assertEquals("Error during data loading.", announcementInserts.size(), this.announcements.size());
        assertEquals("Error during data loading.", calendarInserts.size(), this.calendarItems.size());

        // Do deep equals.
        courseMap = new HashMap<>();
        for (CourseDTO itemDTO : this.courses) {
            courseMap.put(itemDTO.getId(), itemDTO);
        }
    }

    @Test
    public void getOne() throws Exception {
        // Get 5 random items from the list.
        List<AgendaItemDTO> shuffled = new ArrayList<>(this.calendarItems);
        Collections.shuffle(shuffled);
        List<AgendaItemDTO> expected = shuffled.subList(0, 5);

        for (AgendaItemDTO item: expected) {
            AgendaDao.Result result = agendaDao.getOne(item.getId());
            assertEquals(item, result.agendaItem);
            assertThat(result.agendaItem, samePropertyValuesAs(item));
            assertEquals(courseMap.get(item.getCourseId()), result.course);
            assertThat(result.course, samePropertyValuesAs(courseMap.get(result.agendaItem.getCourseId())));
        }
    }

    @Test
    public void getAll() throws Exception {
        List<AgendaDao.Result> results = agendaDao.getAll();
        List<AgendaItemDTO> items = results.stream()
                .map(r -> r.agendaItem)
                .collect(Collectors.toList());
        assertCollectionEquals(this.calendarItems, items);

        // Do deep equals.
        Map<Integer, AgendaItemDTO> expected = new HashMap<>();
        for (AgendaItemDTO itemDTO : this.calendarItems) {
            expected.put(itemDTO.getId(), itemDTO);
        }

        for (AgendaDao.Result actual: results) {
            assertThat(actual.agendaItem, samePropertyValuesAs(expected.get(actual.agendaItem.getId())));
            assertEquals(courseMap.get(actual.agendaItem.getCourseId()), actual.course);
            assertThat(actual.course, samePropertyValuesAs(courseMap.get(actual.agendaItem.getCourseId())));
        }
    }

    @Test
    public void insert() throws Exception {
    }

    @Test
    public void insert1() throws Exception {
    }

    @Test
    public void update() throws Exception {
    }

    @Test
    public void update1() throws Exception {
    }

    @Test
    public void delete() throws Exception {
    }

    @Test
    public void delete1() throws Exception {
    }

    @Test
    public void deleteAll() throws Exception {
    }

    @Test
    public void deleteById() throws Exception {
    }

    @Test
    public void delete2() throws Exception {
    }

    @Test
    public void getAllFutureForCourse() throws Exception {
    }

    @Test
    public void getAllForCourse() throws Exception {
    }

    @Test
    public void getBetween() throws Exception {
    }

    @Test
    public void getCalendarIdsForIds() throws Exception {
    }

}