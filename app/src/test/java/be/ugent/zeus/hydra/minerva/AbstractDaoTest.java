package be.ugent.zeus.hydra.minerva;

import android.arch.persistence.room.Room;
import android.content.Context;

import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementDTO;
import be.ugent.zeus.hydra.minerva.calendar.database.AgendaItemDTO;
import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import org.junit.Before;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static be.ugent.zeus.hydra.testing.Utils.getResourceFile;
import static be.ugent.zeus.hydra.testing.Utils.readJson;
import static org.junit.Assert.assertEquals;

/**
 * Abstract DAO test case. This cannot abstract all logic, since we cannot introduce a DAO interface.
 *
 * @author Niko Strijbol
 */
public abstract class AbstractDaoTest {

    protected Database database;

    protected List<CourseDTO> courses;
    protected Map<String, CourseDTO> courseMap;
    protected List<AnnouncementDTO> announcements;
    protected List<AgendaItemDTO> calendarItems;

    @Before
    public void setUp() throws IOException {
        Context context = RuntimeEnvironment.application;
        database = Room.inMemoryDatabaseBuilder(context, Database.class)
                .allowMainThreadQueries()
                .build();
        fillData();
    }

    private void fillData() throws IOException {
        File courses = getResourceFile("minerva/minerva_courses.sql");
        File announcements = getResourceFile("minerva/minerva_announcements.sql");
        File calendar = getResourceFile("minerva/minerva_calendar.sql");

        List<String> courseInserts = Files.readAllLines(courses.toPath());
        List<String> announcementInserts = Files.readAllLines(announcements.toPath());
        List<String> calendarInserts = Files.readAllLines(calendar.toPath());

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

    private static Moshi getMoshi() {
        return new Moshi.Builder()
                .add(new DateTypeConverters.GsonOffset())
                .add(new DateTypeConverters.GsonInstant())
                .build();
    }

    public static List<CourseDTO> loadTestCourses() throws IOException {
        Type courseType = Types.newParameterizedType(List.class, CourseDTO.class);
        return readJson(getMoshi(), "minerva/minerva_courses.json", courseType);
    }

    public static List<AnnouncementDTO> loadTestAnnouncements() throws IOException {
        Type announcementType = Types.newParameterizedType(List.class, AnnouncementDTO.class);
        return readJson(getMoshi(), "minerva/minerva_announcements.json", announcementType);
    }

    public static List<AgendaItemDTO> loadTestCalendarItems() throws IOException {
        Type calendarType = Types.newParameterizedType(List.class, AgendaItemDTO.class);
        return readJson(getMoshi(), "minerva/minerva_calendar.json", calendarType);
    }
}
