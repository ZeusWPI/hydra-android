package be.ugent.zeus.hydra.common.database.migrations;

import android.app.Instrumentation;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.testing.LocalMigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import java.io.IOException;

import be.ugent.zeus.hydra.common.database.Database;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import static be.ugent.zeus.hydra.common.database.TestUtils.assertContent;
import static be.ugent.zeus.hydra.common.database.TestUtils.assertInstant;
import static be.ugent.zeus.hydra.common.database.TestUtils.assertOffsetDateTime;
import static be.ugent.zeus.hydra.common.database.TestUtils.getFirstFrom;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class TestMigration_10_11 {

    @Rule
    public final LocalMigrationTestHelper testHelper;

    {
        Instrumentation mockInstrumentation = mock(Instrumentation.class);
        when(mockInstrumentation.getTargetContext()).thenReturn(ApplicationProvider.getApplicationContext());
        when(mockInstrumentation.getContext()).thenReturn(ApplicationProvider.getApplicationContext());
        testHelper = new LocalMigrationTestHelper(mockInstrumentation, Database.class.getCanonicalName());
    }

    @Test
    public void testMigration() throws IOException {
        SupportSQLiteDatabase version10 = testHelper.createDatabase("test-db", 10);

        // Load test data manually, since the data types have changed.
        ContentValues expectedCourse = insertCourse(version10);
        ContentValues expectedAnnouncement = insertAnnouncement(version10);
        ContentValues expectedCalendar = insertCalendarItem(version10);
        version10.close();

        SupportSQLiteDatabase version11 = testHelper.runMigrationsAndValidate("test-db", 11, true, new Migration_10_11());
        testAllTypes(expectedCourse, expectedAnnouncement, expectedCalendar, version11);
    }

    @Test
    public void testMigrationNullDates() throws IOException {
        SupportSQLiteDatabase version10 = testHelper.createDatabase("test-db", 10);

        // Load test data manually, since the data types have changed.
        ContentValues expectedCourse = insertCourse(version10);
        ContentValues expectedAnnouncement = insertAnnouncementNoDates(version10);
        ContentValues expectedCalendar = insertCalendarItemNoDates(version10);
        version10.close();

        SupportSQLiteDatabase version11 = testHelper.runMigrationsAndValidate("test-db", 11, true, new Migration_10_11());
        testAllTypes(expectedCourse, expectedAnnouncement, expectedCalendar, version11);
    }

    private static void testAllTypes(ContentValues expectedCourse, ContentValues expectedAnnouncement, ContentValues expectedCalendar, SupportSQLiteDatabase database) {
        // Test the course.
        ContentValues actualCourse = getFirstFrom(database, "minerva_courses");
        assertContent(expectedCourse, actualCourse);

        // Test the announcement.
        ContentValues actualAnnouncement = getFirstFrom(database, "minerva_announcements");
        assertInstant("read_at", expectedAnnouncement, actualAnnouncement);
        assertOffsetDateTime("date", expectedAnnouncement, actualAnnouncement);
        assertContent(expectedAnnouncement, actualAnnouncement);

        // Test the calendar item.
        ContentValues actualCalendar = getFirstFrom(database, "minerva_calendar");
        assertOffsetDateTime("start_date", expectedCalendar, actualCalendar);
        assertOffsetDateTime("end_date", expectedCalendar, actualCalendar);
        assertOffsetDateTime("last_edit", expectedCalendar, actualCalendar);
        assertContent(expectedCalendar, actualCalendar);
    }

    private static ContentValues insertCourse(SupportSQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", "testCourse");
        contentValues.put("code", "TEST");
        contentValues.put("title", "Test");
        contentValues.put("description", "Nothing here.");
        contentValues.put("tutor", "Isaac Newton");
        contentValues.put("academic_year", 2001);
        contentValues.put("ordering", 1);

        database.insert("minerva_courses", SQLiteDatabase.CONFLICT_REPLACE, contentValues);
        return contentValues;
    }

    private static ContentValues insertAnnouncement(SupportSQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", 500);
        contentValues.put("course", "testCourse");
        contentValues.put("title", "Test");
        contentValues.put("content", "Test");
        contentValues.put("email_sent", false);
        contentValues.put("last_edit_user", "TestUser");
        contentValues.put("date", OffsetDateTime.now().toInstant().toEpochMilli());
        contentValues.put("read_at", Instant.now().toEpochMilli());
        database.insert("minerva_announcements", SQLiteDatabase.CONFLICT_REPLACE, contentValues);
        return contentValues;
    }

    private static ContentValues insertAnnouncementNoDates(SupportSQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", 501);
        contentValues.put("course", "testCourse");
        contentValues.put("title", "Test");
        contentValues.put("content", "Test");
        contentValues.put("email_sent", false);
        contentValues.put("last_edit_user", "TestUser");
        contentValues.put("date", -1);
        contentValues.put("read_at", -1);
        database.insert("minerva_announcements", SQLiteDatabase.CONFLICT_REPLACE, contentValues);
        return contentValues;
    }

    private static ContentValues insertCalendarItem(SupportSQLiteDatabase database) {
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
        database.insert("minerva_calendar", SQLiteDatabase.CONFLICT_ABORT, contentValues);
        return contentValues;
    }

    private static ContentValues insertCalendarItemNoDates(SupportSQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", 501);
        contentValues.put("course", "testCourse");
        contentValues.put("title", "Test Event");
        contentValues.put("content", "Nothing");
        contentValues.put("start_date", -1);
        contentValues.put("end_date", -1);
        contentValues.put("location", "Gent");
        contentValues.put("type", "lesson");
        contentValues.put("last_edit_user", "Jules De Stroper");
        contentValues.put("last_edit", -1);
        contentValues.put("last_edit_type", "Removal");
        contentValues.put("calendar_id", 100);
        contentValues.put("is_merged", false);
        database.insert("minerva_calendar", SQLiteDatabase.CONFLICT_ABORT, contentValues);
        return contentValues;
    }
}
