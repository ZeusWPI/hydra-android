package be.ugent.zeus.hydra.data.database.minerva2.agenda;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.database.minerva2.MinervaDatabase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;

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

    private int nrOfTestCourses;
    private int nrOfTestAnnouncements;
    private int nrOfTestCalendarItems;

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
        Resource courses = new ClassPathResource("database/minerva_courses.sql");
        Resource announcements = new ClassPathResource("database/minerva_announcements.sql");
        Resource calendar = new ClassPathResource("database/minerva_calendar.sql");

        List<String> courseInserts = Files.readAllLines(courses.getFile().toPath());
        List<String> announcementInserts = Files.readAllLines(announcements.getFile().toPath());
        List<String> calendarInserts = Files.readAllLines(calendar.getFile().toPath());

        nrOfTestCourses = courseInserts.size();
        nrOfTestAnnouncements = announcementInserts.size();
        nrOfTestCalendarItems = calendarInserts.size();

        Consumer<String> insert = s -> database.compileStatement(s).executeInsert();
        courseInserts.forEach(insert);
        announcementInserts.forEach(insert);
        calendarInserts.forEach(insert);
    }

    @Test
    public void getOne() throws Exception {

    }

    @Test
    public void getAll() throws Exception {
        List<AgendaDao.Result> items = agendaDao.getAll();
        assertEquals(nrOfTestCalendarItems, items.size());
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