package be.ugent.zeus.hydra.common.database.migrations;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Actually adjust the scheme to support Room.
 *
 * The SQL for creating the tables is copied from the scheme exported by Room.
 *
 * @author Niko Strijbol
 */
public class Migration_9_10 extends Migration {

    public Migration_9_10() {
        super(9, 10);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {

        Log.i("Migrations", "Migrating database from " + this.startVersion + " to " + this.endVersion);

        // ---------------------------
        // Adjust the courses table.
        //supportSQLiteDatabase.beginTransaction();
        // Create the new table.
        supportSQLiteDatabase.execSQL(
                "CREATE TABLE `new_minerva_courses` (`_id` TEXT NOT NULL, `code` TEXT, `title` TEXT, `description` TEXT, `tutor` TEXT, `academic_year` INTEGER NOT NULL, `ordering` INTEGER NOT NULL, PRIMARY KEY(`_id`))"
        );

        // Copy the data.
        supportSQLiteDatabase.execSQL(
                "INSERT INTO new_minerva_courses (_id, code, title, description, tutor, academic_year, ordering) SELECT _id, code, title, description, tutor, academic_year, ordering FROM minerva_courses"
        );
        // Drop the old table.
        supportSQLiteDatabase.execSQL("DROP TABLE minerva_courses");
        // Rename the new table.
        supportSQLiteDatabase.execSQL("ALTER TABLE new_minerva_courses RENAME TO minerva_courses");
        // Commit the transaction.
        //supportSQLiteDatabase.endTransaction();
        // ---------------------------

        // ---------------------------
        // Adjust the announcement table.
        //supportSQLiteDatabase.beginTransaction();
        // Create the new table.
        supportSQLiteDatabase.execSQL(
                "CREATE TABLE `new_minerva_announcements` (`title` TEXT, `content` TEXT, `email_sent` INTEGER NOT NULL, `_id` INTEGER NOT NULL, `last_edit_user` TEXT, `date` INTEGER, `read_at` INTEGER, `course` TEXT, PRIMARY KEY(`_id`), FOREIGN KEY(`course`) REFERENCES `minerva_courses`(`_id`) ON UPDATE NO ACTION ON DELETE NO ACTION )"
        );
        // Copy the data.
        supportSQLiteDatabase.execSQL(
                "INSERT INTO new_minerva_announcements (title, content, email_sent, _id, last_edit_user, date, read_at, course) SELECT title, content, email_sent, _id, last_edit_user, date, read_at, course FROM minerva_announcements"

        );
        // Drop the old table.
        supportSQLiteDatabase.execSQL("DROP TABLE minerva_announcements");
        // Rename the new table.
        supportSQLiteDatabase.execSQL("ALTER TABLE new_minerva_announcements RENAME TO minerva_announcements");
        // Add the index.
        supportSQLiteDatabase.execSQL("CREATE  INDEX `index_minerva_announcements_course` ON `minerva_announcements` (`course`)");
        // Commit the transaction.
        //supportSQLiteDatabase.endTransaction();
        // ---------------------------

        // ---------------------------
        // Adjust the calendar table.
        //supportSQLiteDatabase.beginTransaction();
        // Create the new table.
        supportSQLiteDatabase.execSQL(
                "CREATE TABLE `new_minerva_calendar` (`_id` INTEGER NOT NULL, `title` TEXT, `content` TEXT, `start_date` INTEGER, `end_date` INTEGER, `location` TEXT, `type` TEXT, `last_edit_user` TEXT, `last_edit` INTEGER, `last_edit_type` TEXT, `course` TEXT, `calendar_id` INTEGER NOT NULL, `is_merged` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`_id`), FOREIGN KEY(`course`) REFERENCES `minerva_courses`(`_id`) ON UPDATE NO ACTION ON DELETE NO ACTION )"
        );
        // Copy the data.
        supportSQLiteDatabase.execSQL(
                "INSERT INTO new_minerva_calendar (_id, title, content, start_date, end_date, location, type, last_edit_user, last_edit, last_edit_type, course, calendar_id, is_merged) SELECT _id, title, content, start_date, end_date, location, type, last_edit_user, last_edit, last_edit_type, course, calendar_id, is_merged FROM minerva_agenda"
        );
        // Drop the old table.
        supportSQLiteDatabase.execSQL("DROP TABLE minerva_agenda");
        // Rename the new table.
        supportSQLiteDatabase.execSQL("ALTER TABLE new_minerva_calendar RENAME TO minerva_calendar");
        // Create the index.
        supportSQLiteDatabase.execSQL("CREATE  INDEX `index_minerva_calendar_course` ON `minerva_calendar` (`course`)");
        // Commit transaction.
        //supportSQLiteDatabase.endTransaction();
        // ---------------------------

        Log.i("Migrations", "Migration is completed.");
    }
}