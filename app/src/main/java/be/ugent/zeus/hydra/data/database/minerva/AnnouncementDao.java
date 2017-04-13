package be.ugent.zeus.hydra.data.database.minerva;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.util.Log;

import be.ugent.zeus.hydra.data.database.Dao;
import be.ugent.zeus.hydra.data.database.DiffDao;
import be.ugent.zeus.hydra.data.database.Utils;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.utils.TtbUtils;
import org.threeten.bp.ZonedDateTime;

import java.util.*;

import static be.ugent.zeus.hydra.data.database.Utils.*;

/**
 * Dao to access announcements from the database.
 *
 * @author Niko Strijbol
 */
public class AnnouncementDao extends Dao implements DiffDao<Announcement, Integer> {

    private static final String TAG = "AnnouncementDao";

    /**
     * @param context The application context.
     */
    public AnnouncementDao(Context context) {
        super(context);
    }

    /**
     * Get values for an announcement.
     *
     * @param a The announcement.
     *
     * @return The values.
     */
    private static ContentValues getValues(Announcement a) {
        ContentValues values = new ContentValues();

        values.put(AnnouncementTable.Columns.ID, a.getItemId());
        values.put(AnnouncementTable.Columns.COURSE, a.getCourse().getId());
        values.put(AnnouncementTable.Columns.TITLE, a.getTitle());
        values.put(AnnouncementTable.Columns.CONTENT, a.getContent());
        values.put(AnnouncementTable.Columns.EMAIL_SENT, Utils.boolToInt(a.isEmailSent()));
        values.put(AnnouncementTable.Columns.STICKY_UNTIL, 0);
        values.put(AnnouncementTable.Columns.LECTURER, a.getLecturer());
        values.put(AnnouncementTable.Columns.DATE, TtbUtils.serialize(a.getDate()));
        values.put(AnnouncementTable.Columns.READ_DATE, TtbUtils.serialize(a.getRead()));

        return values;
    }

    /**
     * Delete all data.
     */
    public void deleteAll() {
        helper.getWritableDatabase().delete(AnnouncementTable.TABLE_NAME, null, null);
    }

    /**
     * Update an announcement. This should not be called on the UI thread.
     *
     * @param a The announcement to update.
     */
    public void update(final Announcement a) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = getValues(a);
        db.update(
                AnnouncementTable.TABLE_NAME,
                values,
                AnnouncementTable.Columns.ID + " = ?",
                new String[]{String.valueOf(a.getItemId())}
        );

        Log.i(TAG, "Updated announcement " + a.getItemId());
        // Prepare to send the broadcast.
        Bundle extras = new Bundle();
        extras.putInt(DatabaseBroadcaster.ARG_MINERVA_ANNOUNCEMENT_ID, a.getItemId());
        extras.putString(DatabaseBroadcaster.ARG_MINERVA_ANNOUNCEMENT_COURSE, a.getCourse().getId());
        broadcaster.publishIntentWith(DatabaseBroadcaster.MINERVA_ANNOUNCEMENT_UPDATED, extras);
    }

    /**
     * Update an announcement. This should not be called on the UI thread.
     *
     * @param a The announcement to update.
     */
    public void insert(final Announcement a) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = getValues(a);
        db.insertOrThrow(AnnouncementTable.TABLE_NAME, null, values);
        Log.i(TAG, "Added announcement " + a.getItemId());
    }

    /**
     * Get a list of ids of the announcements for a course in the database.
     *
     * @param course  The course.
     * @param reverse If the announcements should be reversed (newest first) or not.
     *
     * @return List of ids in the database.
     */
    public List<Announcement> getAnnouncementsForCourse(Course course, boolean reverse) {

        SQLiteDatabase db = helper.getReadableDatabase();
        List<Announcement> result = new ArrayList<>();

        String order = AnnouncementTable.Columns.DATE;
        if (reverse) {
            order += " DESC";
        } else {
            order += " ASC";
        }

        Cursor cursor = db.query(
                AnnouncementTable.TABLE_NAME,
                null,
                AnnouncementTable.Columns.COURSE + " = ?",
                new String[]{course.getId()},
                null, null, order);

        if (cursor == null) {
            return result;
        }

        //Get helper
        AnnouncementExtractor extractor = new AnnouncementExtractor.Builder(cursor)
                .defaults().build();

        try {
            while (cursor.moveToNext()) {
                result.add(extractor.getAnnouncement(course));
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    public Map<Integer, ZonedDateTime> getIdsAndReadDateForCourse(Course course) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                AnnouncementTable.TABLE_NAME,
                new String[]{AnnouncementTable.Columns.ID, AnnouncementTable.Columns.READ_DATE},
                where(AnnouncementTable.Columns.COURSE),
                args(course.getId()),
                null,
                null,
                null
        );

        //TODO: maybe use android SparseIntArray?
        Map<Integer, ZonedDateTime> ids = new HashMap<>();

        if (cursor == null) {
            return ids;
        }

        try {
            while (cursor.moveToNext()) {
                ids.put(
                        cursor.getInt(cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.ID)),
                        TtbUtils.unserialize(cursor.getLong(cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.READ_DATE)))

                );
            }
        } finally {
            cursor.close();
        }

        return ids;
    }

    /**
     * Get a list of ids of the announcements for a course in the database.
     *
     * @return List of ids in the database.
     */
    public Map<Course, List<Announcement>> getUnread() {

        SQLiteDatabase db = helper.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        final String courseTable = "course_";

        String announcementJoin = AnnouncementTable.Columns.COURSE;
        String courseJoin = courseTable + CourseTable.Columns.ID;

        builder.setTables(AnnouncementTable.TABLE_NAME + " INNER JOIN " + CourseTable.TABLE_NAME + " ON " + announcementJoin + "=" + courseJoin);

        Map<Course, List<Announcement>> map = new HashMap<>();

        String[] columns = new String[]{
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.Columns.ID,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.Columns.TITLE,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.Columns.CONTENT,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.Columns.EMAIL_SENT,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.Columns.STICKY_UNTIL,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.Columns.LECTURER,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.Columns.DATE,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.Columns.READ_DATE,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.ID + " AS " + courseTable + CourseTable.Columns.ID,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.CODE + " AS " + courseTable + CourseTable.Columns.CODE,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.TITLE + " AS " + courseTable + CourseTable.Columns.TITLE,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.DESCRIPTION + " AS " + courseTable + CourseTable.Columns.DESCRIPTION,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.TUTOR + " AS " + courseTable + CourseTable.Columns.TUTOR,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.STUDENT + " AS " + courseTable + CourseTable.Columns.STUDENT,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.ACADEMIC_YEAR + " AS " + courseTable + CourseTable.Columns.ACADEMIC_YEAR,
        };

        Cursor c = builder.query(
                db,
                columns,
                AnnouncementTable.Columns.READ_DATE + " = ?",
                new String[]{"-1"},
                null,
                null,
                AnnouncementTable.Columns.COURSE + " ASC, " + AnnouncementTable.Columns.DATE + " DESC"
        );

        if (c == null) {
            return map;
        }

        //Save the course ID separately
        //Get helpers
        CourseExtractor cExtractor = new CourseExtractor.Builder(c)
                .columnId(courseTable + CourseTable.Columns.ID)
                .columnCode(courseTable + CourseTable.Columns.CODE)
                .columnTitle(courseTable + CourseTable.Columns.TITLE)
                .columnDesc(courseTable + CourseTable.Columns.DESCRIPTION)
                .columnTutor(courseTable + CourseTable.Columns.TUTOR)
                .columnStudent(courseTable + CourseTable.Columns.STUDENT)
                .columnYear(courseTable + CourseTable.Columns.ACADEMIC_YEAR)
                .build();
        AnnouncementExtractor aExtractor = new AnnouncementExtractor.Builder(c).defaults().build();

        try {
            Course currentCourse = null;
            int counter = 0;
            while (c.moveToNext()) {

                //Get the course id
                String id = c.getString(cExtractor.getColumnId());

                if (currentCourse == null || !currentCourse.getId().equals(id)) {
                    //Add the course
                    currentCourse = cExtractor.getCourse();
                    map.put(currentCourse, new ArrayList<>());
                    counter = 0;
                }

                map.get(currentCourse).add(aExtractor.getAnnouncement(currentCourse));
                counter++;
            }

        } finally {
            c.close();
        }

        return map;
    }

    @Override
    public void delete(Collection<Integer> ids) {
        SQLiteDatabase db = helper.getWritableDatabase();

        if (ids == null) {
            db.delete(AnnouncementTable.TABLE_NAME, null, null);
        } else if (ids.size() == 1) {
            db.delete(AnnouncementTable.TABLE_NAME, where(AnnouncementTable.Columns.ID), args(ids));
        } else {
            db.delete(AnnouncementTable.TABLE_NAME, in(AnnouncementTable.Columns.ID, ids.size()), args(ids));
        }
    }

    @Override
    public void update(Collection<Announcement> elements) {
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (Announcement item: elements) {
                ContentValues values = getValues(item);
                values.remove(AnnouncementTable.Columns.ID);
                db.update(AnnouncementTable.TABLE_NAME, values, where(AnnouncementTable.Columns.ID), args(item.getItemId()));
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void insert(Collection<Announcement> elements) {
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();
            for (Announcement agendaItem: elements) {
                db.insertOrThrow(AnnouncementTable.TABLE_NAME, null, getValues(agendaItem));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}