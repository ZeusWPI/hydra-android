package be.ugent.zeus.hydra.common.database.migrations;

import android.app.Instrumentation;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.room.testing.LocalMigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import java.io.IOException;

import be.ugent.zeus.hydra.common.database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import static be.ugent.zeus.hydra.common.database.TestUtils.assertContent;
import static be.ugent.zeus.hydra.common.database.TestUtils.getFirstFrom;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the migration from the raw SQL database to using room.
 * Since it involves raw SQL, the test is slightly more involved than future migration tests.
 *
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class TestMigration_9_10 {

    private static final String TEST_DATABASE_NAME = "test-db";

    @Rule
    public final LocalMigrationTestHelper testHelper;

    {
        Instrumentation mockInstrumentation = mock(Instrumentation.class);
        when(mockInstrumentation.getTargetContext()).thenReturn(ApplicationProvider.getApplicationContext());
        when(mockInstrumentation.getContext()).thenReturn(ApplicationProvider.getApplicationContext());
        testHelper = new LocalMigrationTestHelper(mockInstrumentation, Database.class.getCanonicalName());
    }

    private SQLiteOpenHelper version9Helper;

    @Before
    public void setUp() {
        version9Helper = new Version9SqlHelper(ApplicationProvider.getApplicationContext(), TEST_DATABASE_NAME);
        // Ensure tables are made.
        Version9SqlHelper.createTables(version9Helper.getWritableDatabase());
    }

    @After
    public void tearDown() {
        // Ensure database is cleared.
        Version9SqlHelper.deleteTables(version9Helper.getWritableDatabase());
    }

    @Test
    public void testMigration() throws IOException {
        // First we insert some data into the database.
        SQLiteDatabase database = version9Helper.getWritableDatabase();
        ContentValues expectedCourse = insertCourse(database);
        ContentValues expectedAnnouncement = insertAnnouncement(database);
        ContentValues expectedCalendarItem = insertCalendarItem(database);
        database.close();

        // Get the new database and test if the data survived and is the same.
        SupportSQLiteDatabase newDatabase = testHelper.runMigrationsAndValidate(TEST_DATABASE_NAME, 10,true, new Migration_9_10());

        assertContent(expectedCourse, getFirstFrom(newDatabase, "minerva_courses"));
        assertContent(expectedAnnouncement, getFirstFrom(newDatabase, "minerva_announcements"));
        assertContent(expectedCalendarItem, getFirstFrom(newDatabase, "minerva_calendar"));
    }

    private static ContentValues insertCourse(SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", "testCourse");
        contentValues.put("code", "TEST");
        contentValues.put("title", "Test");
        contentValues.put("description", "Nothing here.");
        contentValues.put("tutor", "Isaac Newton");
        contentValues.put("academic_year", 2001);
        contentValues.put("ordering", 1);
        database.insert("minerva_courses",  null, contentValues);
        return contentValues;
    }

    private static ContentValues insertAnnouncement(SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", 500);
        contentValues.put("course", "testCourse");
        contentValues.put("title", "Test");
        contentValues.put("content", "Test");
        contentValues.put("email_sent", false);
        contentValues.put("last_edit_user", "TestUser");
        contentValues.put("date", OffsetDateTime.now().toInstant().toEpochMilli());
        contentValues.put("read_at", Instant.now().toEpochMilli());
        database.insert("minerva_announcements", null, contentValues);
        return contentValues;
    }

    private static ContentValues insertCalendarItem(SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", 500);
        contentValues.put("course", "testCourse");
        contentValues.put("title", "Test Event");
        contentValues.put("content", "Nothing");
        contentValues.put("start_date", Instant.now().toEpochMilli());
        contentValues.put("end_date", Instant.now().plusSeconds(60).toEpochMilli());
        contentValues.put("location", "Gent");
        contentValues.put("type", "lesson");
        contentValues.put("last_edit_user", "Jules De Stroper");
        contentValues.put("last_edit", Instant.now().toEpochMilli());
        contentValues.put("last_edit_type", "Removal");
        contentValues.put("calendar_id", 100);
        contentValues.put("is_merged", false);
        database.insert("minerva_agenda", null, contentValues);
        return contentValues;
    }
}
