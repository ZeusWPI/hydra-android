package be.ugent.zeus.hydra.minerva.announcement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import be.ugent.zeus.hydra.minerva.database.Dao;
import be.ugent.zeus.hydra.minerva.database.DatabaseHelper;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.*;

/**
 * Dao to access announcements from the database.
 *
 * @author Niko Strijbol
 */
public class AnnouncementDao implements Dao<Announcement> {

    private static final String TAG = "AnnouncementDao";

    private final DatabaseHelper helper;
    private final Context context;

    /**
     * @param context The application context.
     */
    public AnnouncementDao(Context context) {
        this.helper = DatabaseHelper.getInstance(context);
        this.context = context;
    }

    /**
     * Delete all data.
     */
    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(AnnouncementTable.TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    /**
     * Synchronise announcements for one course.
     *
     * @param announcements The announcements.
     *
     * @param first If this is the first sync or not.
     *
     * @return The new announcements.
     */
    public Collection<Announcement> synchronisePartial(Collection<Announcement> announcements, Course course, boolean first) {

        SQLiteDatabase db = helper.getWritableDatabase();

        //Get existing courses.
        Set<Integer> present = getIdsForCourse(db, course);
        Set<Announcement> newAnnouncements = new HashSet<>();

        int counter = 0;
        try {
            db.beginTransaction();

            //Delete old courses
            String ids = TextUtils.join(", ", getRemovable(present, announcements));
            db.delete(AnnouncementTable.TABLE_NAME, AnnouncementTable.COLUMN_ID + " IN (?)", new String[]{ids});

            Date date = new Date();
            for (Announcement announcement: announcements ) {

                announcement.setCourse(course);
                ContentValues value = getValues(announcement);

                //Update the announcement
                if(present.contains(announcement.getItemId())) {
                    value.remove(AnnouncementTable.COLUMN_ID);
                    db.update(
                            AnnouncementTable.TABLE_NAME,
                            value,
                            AnnouncementTable.COLUMN_ID + " = ?",
                            new String[]{String.valueOf(announcement.getItemId())}
                            );
                }
                //Add new announcement
                else {
                    if(first) {
                        value.put(AnnouncementTable.COLUMN_READ_DATE, date.getTime());
                    } else {
                        newAnnouncements.add(announcement);
                    }
                    db.insertOrThrow(AnnouncementTable.TABLE_NAME, null, value);
                    counter++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        Log.d(TAG, "New announcements for " + course.getTitle() + ": " + counter);
        return newAnnouncements;
    }

    private static ContentValues getValues(Announcement a) {
        ContentValues values = new ContentValues();

        values.put(AnnouncementTable.COLUMN_ID, a.getItemId());
        values.put(AnnouncementTable.COLUMN_COURSE, a.getCourse().getId());
        values.put(AnnouncementTable.COLUMN_TITLE, a.getTitle());
        values.put(AnnouncementTable.COLUMN_CONTENT, a.getContent());
        values.put(AnnouncementTable.COLUMN_EMAIL_SENT, boolToInt(a.isEmailSent()));
        values.put(AnnouncementTable.COLUMN_STICKY_UNTIL, 0);
        values.put(AnnouncementTable.COLUMN_LECTURER, a.getLecturer());
        values.put(AnnouncementTable.COLUMN_DATE, a.getDate().getTime());
        values.put(AnnouncementTable.COLUMN_READ_DATE, 0);
        values.put(AnnouncementTable.COLUMN_NOTIFICATION, 0);

        return values;
    }

    private static boolean intToBool(int integer) {
        return integer == 1;
    }

    private static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    /**
     * A set of ids that are not in the course.
     *
     * @param ids Ids of local courses.
     * @param announcements Remote announcements.
     * @return Local courses that can be deleted.
     */
    private static Set<Integer> getRemovable(final Set<Integer> ids, final Collection<Announcement> announcements) {
        Set<Integer> removable = new HashSet<>(ids);
        //Iterate the course to prevent O(n^2)
        for (Announcement announcement: announcements) {
            if(removable.contains(announcement.getItemId())) {
                removable.remove(announcement.getItemId());
            }
        }

        return removable;
    }

    /**
     * Get a list of ids of the announcements for a course in the database.
     *
     * @param db The database.
     *
     * @return List of ids in the database.
     */
    private static Set<Integer> getIdsForCourse(SQLiteDatabase db, Course course) {

        Cursor cursor = db.query(
                AnnouncementTable.TABLE_NAME,
                new String[] {AnnouncementTable.COLUMN_ID},
                AnnouncementTable.COLUMN_COURSE + " = ?",
                new String[]{course.getId()},
                null, null, null);

        Set<Integer> result = new HashSet<>();

        if(cursor != null) {
            try {
                int columnIndex = cursor.getColumnIndex(AnnouncementTable.COLUMN_ID);

                while (cursor.moveToNext()) {
                    result.add(cursor.getInt(columnIndex));
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }

    /**
     * Get a list of ids of the announcements for a course in the database.
     *
     * @param course The course.
     * @param reverse If the announcements should be reversed (newest first) or not.
     *
     * @return List of ids in the database.
     */
    public List<Announcement> getAnnouncementsForCourse(Course course, boolean reverse) {

        SQLiteDatabase db = helper.getReadableDatabase();
        List<Announcement> result = new ArrayList<>();

        String order = AnnouncementTable.COLUMN_DATE;
        if(reverse) {
            order += " DESC";
        } else {
            order += " ASC";
        }

        try {
            Cursor cursor = db.query(
                    AnnouncementTable.TABLE_NAME,
                    null,
                    AnnouncementTable.COLUMN_COURSE + " = ?",
                    new String[]{course.getId()},
                    null, null, order);

            if (cursor != null) {
                try {
                    int columnIndex = cursor.getColumnIndex(AnnouncementTable.COLUMN_ID);
                    int columnTitle = cursor.getColumnIndex(AnnouncementTable.COLUMN_TITLE);
                    int columnContent = cursor.getColumnIndex(AnnouncementTable.COLUMN_CONTENT);
                    int columnEmailSent = cursor.getColumnIndex(AnnouncementTable.COLUMN_EMAIL_SENT);
                    int columnSticky = cursor.getColumnIndex(AnnouncementTable.COLUMN_STICKY_UNTIL);
                    int columnLecturer = cursor.getColumnIndex(AnnouncementTable.COLUMN_LECTURER);
                    int columnDate = cursor.getColumnIndex(AnnouncementTable.COLUMN_DATE);

                    while (cursor.moveToNext()) {
                        Announcement a = new Announcement();
                        a.setCourse(course);
                        a.setItemId(cursor.getInt(columnIndex));
                        a.setTitle(cursor.getString(columnTitle));
                        a.setContent(cursor.getString(columnContent));
                        a.setEmailSent(intToBool(cursor.getInt(columnEmailSent)));
                        a.setLecturer(cursor.getString(columnLecturer));
                        a.setDate(new Date(cursor.getLong(columnDate)));
                        result.add(a);
                    }
                } finally {
                    cursor.close();
                }
            }
        } finally {
            db.close();
        }

        return result;
    }

    /**
     * Get a list of ids of the announcements for a course in the database.
     *
     * @param course The course.
     *
     * @return List of ids in the database.
     */
    public List<Announcement> getAnnouncementsForCourse(Course course) {
        return getAnnouncementsForCourse(course, false);
    }

    /**
     * @return All elements in this dao.
     */
    @NonNull
    @Override
    public List<Announcement> getAll() {
        return null;
    }
}
