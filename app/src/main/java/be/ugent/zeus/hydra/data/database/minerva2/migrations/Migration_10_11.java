package be.ugent.zeus.hydra.data.database.minerva2.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Migrate the types of the dates.
 *
 * This migration contains some methods, like {@link #legacyUnserialize(long)}, which are copied into this class,
 * to make sure the migration doesn't have any dependencies on old code.
 *
 * Similarly, the new methods are also copied into this class. This prevents accidental incompatible changes to the code.
 *
 * @author Niko Strijbol
 */
public class Migration_10_11 extends Migration {

    private static final String TAG = "Migration_10_11";

    public Migration_10_11() {
        super(10, 11);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

        Log.i(TAG, "Migrating database from " + this.startVersion + " to " + this.endVersion);

        // The courses table has not changed.

        // Adjust the announcement table.
        // Create the new table.
        database.execSQL(
                "CREATE TABLE `new_minerva_announcements` (`title` TEXT, `content` TEXT, `email_sent` INTEGER NOT NULL, `_id` INTEGER NOT NULL, `last_edit_user` TEXT, `date` TEXT, `read_at` TEXT, `course` TEXT, PRIMARY KEY(`_id`), FOREIGN KEY(`course`) REFERENCES `minerva_courses`(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
        );
        // We cannot just copy the data, since we need to modify the format of the data.
        Cursor cursor = database.query("SELECT * FROM minerva_announcements");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
                // We need to adjust the 'date' and 'read_at' field.
                ZonedDateTime originalDate = legacyUnserialize(contentValues.getAsLong("date"));
                if (originalDate != null) {
                    OffsetDateTime newDate = originalDate.toOffsetDateTime();
                    contentValues.put("date", fromOffsetDateTime(newDate));
                } else {
                    contentValues.put("date", (String) null);
                }
                ZonedDateTime originalReadDate = legacyUnserialize(contentValues.getAsLong("read_at"));
                if (originalReadDate != null) {
                    Instant newReadDate = originalReadDate.toInstant();
                    contentValues.put("read_at", fromInstant(newReadDate));
                } else {
                    contentValues.put("read_at", (String) null);
                }

                // Insert the row into the new table.
                database.insert("new_minerva_announcements", SQLiteDatabase.CONFLICT_NONE, contentValues);
            }
        } else {
            Log.w(TAG, "Cursor for announcements is null, skipping data conversion.");
        }
        // Drop the old table.
        database.execSQL("DROP TABLE minerva_announcements");
        // Rename the new table.
        database.execSQL("ALTER TABLE `new_minerva_announcements` RENAME TO `minerva_announcements`");
        // Add the index.
        database.execSQL("CREATE  INDEX `index_minerva_announcements_course` ON `minerva_announcements` (`course`)");

        // We do the same for the calendar table.
        // Create the new table.
        database.execSQL(
                "CREATE TABLE `new_minerva_calendar` (`_id` INTEGER NOT NULL, `title` TEXT, `content` TEXT, `start_date` TEXT, `end_date` TEXT, `location` TEXT, `type` TEXT, `last_edit_user` TEXT, `last_edit` TEXT, `last_edit_type` TEXT, `course` TEXT, `calendar_id` INTEGER NOT NULL, `is_merged` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`_id`), FOREIGN KEY(`course`) REFERENCES `minerva_courses`(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
        );
        // We cannot just copy the data, since we need to modify the format of the data.
        Cursor calendarCursor = database.query("SELECT * FROM minerva_calendar");
        if (calendarCursor != null) {
            while (calendarCursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(calendarCursor, contentValues);
                // We must convert the start_date and end_date.
                ZonedDateTime originalStartDate = legacyUnserialize(contentValues.getAsLong("start_date"));
                if (originalStartDate != null) {
                    OffsetDateTime newStartDate = originalStartDate.toOffsetDateTime();
                    contentValues.put("start_date", fromOffsetDateTime(newStartDate));
                } else {
                    contentValues.put("start_date", (String) null);
                }
                ZonedDateTime originalEndDate = legacyUnserialize(contentValues.getAsLong("end_date"));
                if (originalEndDate != null) {
                    OffsetDateTime newEndDate = originalEndDate.toOffsetDateTime();
                    contentValues.put("end_date", fromOffsetDateTime(newEndDate));
                } else {
                    contentValues.put("end_date", (String) null);
                }
            }
        } else {
            Log.w(TAG, "Cursor for calendar is null, skipping data conversion.");
        }
        // Drop the old table.
        database.execSQL("DROP TABLE minerva_calendar");
        // Rename the new table.
        database.execSQL("ALTER TABLE `new_minerva_calendar` RENAME TO minerva_calendar");
        // Add the index.
        database.execSQL("CREATE  INDEX `index_minerva_calendar_course` ON `minerva_calendar` (`course`)");

        Log.i(TAG, "Migration completed.");
    }

    private static final ZoneOffset ZONE = ZoneOffset.UTC;
    private static DateTimeFormatter OFFSET_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Unserialize a calculated epoch to a ZonedDateTime.
     *
     * @return A ZonedDateTime representing the epoch milli.
     */
    private static ZonedDateTime legacyUnserialize(long epochMilli) {
        if (epochMilli == -1) {
            return null;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(ZONE);
    }

    /**
     * Converts an instant to a string.
     *
     * @param instant The instant.
     *
     * @return The string value.
     */
    private static String fromInstant(Instant instant) {
        if (instant == null) {
            return null;
        } else {
            return instant.toString();
        }
    }

    /**
     * Converts a offset date time to a string in the format specified by {@link #OFFSET_FORMATTER}.
     *
     * @param dateTime The date time or {@code null}.
     *
     * @return The string or {@code null} if the input was {@code null}.
     */
    private static String fromOffsetDateTime(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return dateTime.format(OFFSET_FORMATTER);
        }
    }
}