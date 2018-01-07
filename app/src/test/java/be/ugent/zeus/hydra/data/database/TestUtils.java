package be.ugent.zeus.hydra.data.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
public class TestUtils {

    public static ContentValues getFirstFrom(SupportSQLiteDatabase database, String table) {
        Cursor cursor = database.query("SELECT * FROM " + table);
        assertNotNull(cursor);
        assertTrue(cursor.moveToNext());
        ContentValues result = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, result);
        cursor.close();
        return result;
    }

    public static void assertOffsetDateTime(String field, ContentValues expected, ContentValues actual) {
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

    public static void assertInstant(String field, ContentValues expected, ContentValues actual) {
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

    /**
     * We assume the expected values have the correct type.
     * @param expected The expected value.
     * @param actual The new values.
     */
    @SuppressWarnings("IfStatementWithTooManyBranches")
    public static void assertContent(ContentValues expected, ContentValues actual) {
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
}
