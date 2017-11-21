package be.ugent.zeus.hydra.data.database.minerva2;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.dto.minerva.AgendaItemDTO;
import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import be.ugent.zeus.hydra.data.gson.ZonedThreeTenTimeStampAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.Before;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

/**
 * Abstract DAO test case. This cannot abstract all logic, since we cannot introduce a DAO interface.
 *
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public abstract class AbstractDaoTest {

    protected MinervaDatabase database;

    protected List<CourseDTO> courses;
    protected Map<String, CourseDTO> courseMap;
    protected List<AnnouncementDTO> announcements;
    protected List<AgendaItemDTO> calendarItems;

    @Before
    @CallSuper
    public void setUp() throws IOException {
        Context context = RuntimeEnvironment.application;
        database = Room.inMemoryDatabaseBuilder(context, MinervaDatabase.class)
                .allowMainThreadQueries()
                .build();
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

        this.courses = loadTestCourses();
        this.announcements = loadTestAnnouncements();
        this.calendarItems = loadTestCalendarItems();

        assertEquals("Error during data loading.", courseInserts.size(), this.courses.size());
        assertEquals("Error during data loading.", announcementInserts.size(), this.announcements.size());
        assertEquals("Error during data loading.", calendarInserts.size(), this.calendarItems.size());

        // Do deep equals.
        courseMap = new HashMap<>();
        for (CourseDTO itemDTO : this.courses) {
            courseMap.put(itemDTO.getId(), itemDTO);
        }
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedThreeTenTimeStampAdapter())
                .create();
    }

    public static List<CourseDTO> loadTestCourses() throws IOException {
        Resource jsonCourses = new ClassPathResource("minerva/minerva_courses.json");
        Type courseType = new TypeToken<List<CourseDTO>>() {}.getType();
        return getGson().fromJson(new JsonReader(new FileReader(jsonCourses.getFile())), courseType);
    }

    public static List<AnnouncementDTO> loadTestAnnouncements() throws IOException {
        Resource jsonAnnouncements = new ClassPathResource("minerva/minerva_announcements.json");
        Type announcementType = new TypeToken<List<AnnouncementDTO>>() {}.getType();
        return getGson().fromJson(new JsonReader(new FileReader(jsonAnnouncements.getFile())), announcementType);
    }

    public static List<AgendaItemDTO> loadTestCalendarItems() throws IOException {
        Resource jsonCalendar = new ClassPathResource("minerva/minerva_calendar.json");
        Type calendarType = new TypeToken<List<AgendaItemDTO>>() {}.getType();
        return getGson().fromJson(new JsonReader(new FileReader(jsonCalendar.getFile())), calendarType);
    }
}
