package be.ugent.zeus.hydra.data.database.migrations;

import android.app.Instrumentation;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.testing.LocalMigrationTestHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.data.database.Database;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class TestMigration_10_11 {

    @Rule
    public LocalMigrationTestHelper testHelper;

    {
        Instrumentation mockInstrumentation = mock(Instrumentation.class);
        when(mockInstrumentation.getTargetContext()).thenReturn(RuntimeEnvironment.application);
        when(mockInstrumentation.getContext()).thenReturn(RuntimeEnvironment.application);
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
        version11.close();
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
        version11.close();
    }

    private void testAllTypes(ContentValues expectedCourse, ContentValues expectedAnnouncement, ContentValues expectedCalendar, SupportSQLiteDatabase database) {
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

    /**
     * We assume the expected values have the correct type.
     * @param expected The expected value.
     * @param actual The new values.
     */
    private void assertContent(ContentValues expected, ContentValues actual) {
        assertEquals(expected.size(), actual.size());
        for (Map.Entry<String, Object> entry : expected.valueSet()) {
            if (entry.getValue() instanceof Integer) {
                assertEquals(entry.getValue(), actual.getAsInteger(entry.getKey()));
            } else if (entry.getValue() instanceof Long) {
                assertEquals(entry.getValue(), actual.getAsLong(entry.getKey()));
            } else if (entry.getValue() instanceof Boolean) {
                assertEquals(entry.getValue(), actual.getAsBoolean(entry.getKey()));
            } else if (entry.getValue() instanceof Double) {
                assertEquals(entry.getValue(), actual.getAsDouble(entry.getKey()));
            } else if (entry.getValue() instanceof Float) {
                assertEquals(entry.getValue(), actual.getAsFloat(entry.getKey()));
            } else {
                assertEquals(entry.getValue(), actual.get(entry.getKey()));
            }
        }
    }

    private ContentValues getFirstFrom(SupportSQLiteDatabase database, String table) {
        Cursor cursor = database.query("SELECT * FROM " + table);
        assertNotNull(cursor);
        assertTrue(cursor.moveToNext());
        ContentValues result = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, result);
        cursor.close();
        return result;
    }

    private void assertOffsetDateTime(String field, ContentValues expected, ContentValues actual) {
        String actualRaw = actual.getAsString(field);
        long expectedRaw = expected.getAsLong(field);
        if (expectedRaw == -1) {
            assertNull(actualRaw);
        } else {
            assertNotNull(actualRaw);
            OffsetDateTime actualDate = OffsetDateTime.parse(actualRaw, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime expectedDate = OffsetDateTime.ofInstant(Instant.ofEpochMilli(expectedRaw), ZoneId.systemDefault());
            assertEquals(expectedDate.toInstant(), actualDate.toInstant());
        }
        expected.remove(field);
        actual.remove(field);
    }

    @SuppressWarnings("SameParameterValue")
    private void assertInstant(String field, ContentValues expected, ContentValues actual) {
        String actualRaw = actual.getAsString(field);
        long expectedRaw = expected.getAsLong(field);
        if (expectedRaw == -1) {
            assertNull(actualRaw);
        } else {
            assertNotNull(actualRaw);
            Instant actualDate = Instant.parse(actualRaw);
            Instant expectedDate = Instant.ofEpochMilli(expectedRaw);
            assertEquals(expectedDate, actualDate);
        }
        expected.remove(field);
        actual.remove(field);
    }

    private ContentValues insertCourse(SupportSQLiteDatabase database) {
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

    private ContentValues insertAnnouncement(SupportSQLiteDatabase database) {
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

    private ContentValues insertAnnouncementNoDates(SupportSQLiteDatabase database) {
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

    private ContentValues insertCalendarItem(SupportSQLiteDatabase database) {
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

    private ContentValues insertCalendarItemNoDates(SupportSQLiteDatabase database) {
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