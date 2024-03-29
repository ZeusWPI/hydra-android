/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.common.database.migrations;

import android.app.Instrumentation;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.room.testing.LocalMigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;

import be.ugent.zeus.hydra.common.database.Database;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
    private SQLiteOpenHelper version9Helper;

    {
        Instrumentation mockInstrumentation = mock(Instrumentation.class);
        when(mockInstrumentation.getTargetContext()).thenReturn(ApplicationProvider.getApplicationContext());
        when(mockInstrumentation.getContext()).thenReturn(ApplicationProvider.getApplicationContext());
        testHelper = new LocalMigrationTestHelper(mockInstrumentation, Database.class.getCanonicalName());
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
        database.insert("minerva_courses", null, contentValues);
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
        SupportSQLiteDatabase newDatabase = testHelper.runMigrationsAndValidate(TEST_DATABASE_NAME, 10, true, new Migration_9_10());

        assertContent(expectedCourse, getFirstFrom(newDatabase, "minerva_courses"));
        assertContent(expectedAnnouncement, getFirstFrom(newDatabase, "minerva_announcements"));
        assertContent(expectedCalendarItem, getFirstFrom(newDatabase, "minerva_calendar"));
    }
}
