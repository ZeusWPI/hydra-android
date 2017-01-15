package be.ugent.zeus.hydra.minerva.announcement;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.preference.PreferenceManager;
import android.util.Log;
import be.ugent.zeus.hydra.fragments.preferences.MinervaFragment;
import be.ugent.zeus.hydra.minerva.course.CourseExtractor;
import be.ugent.zeus.hydra.minerva.course.CourseTable;
import be.ugent.zeus.hydra.minerva.database.Dao;
import be.ugent.zeus.hydra.minerva.database.Utils;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.utils.TtbUtils;
import org.threeten.bp.ZonedDateTime;

import java.util.*;

/**
 * Dao to access announcements from the database.
 *
 * @author Niko Strijbol
 */
public class AnnouncementDao extends Dao {

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

        values.put(AnnouncementTable.COLUMN_ID, a.getItemId());
        values.put(AnnouncementTable.COLUMN_COURSE, a.getCourse().getId());
        values.put(AnnouncementTable.COLUMN_TITLE, a.getTitle());
        values.put(AnnouncementTable.COLUMN_CONTENT, a.getContent());
        values.put(AnnouncementTable.COLUMN_EMAIL_SENT, Utils.boolToInt(a.isEmailSent()));
        values.put(AnnouncementTable.COLUMN_STICKY_UNTIL, 0);
        values.put(AnnouncementTable.COLUMN_LECTURER, a.getLecturer());
        values.put(AnnouncementTable.COLUMN_DATE, TtbUtils.serialize(a.getDate()));
        values.put(AnnouncementTable.COLUMN_READ_DATE, TtbUtils.serialize(a.getRead()));

        return values;
    }

    /**
     * Delete all data.
     */
    public void deleteAll() {
        helper.getWritableDatabase().delete(AnnouncementTable.TABLE_NAME, null, null);
    }

    /**
     * Synchronize the announcements based on a sync object.
     *
     * @param object The sync object.
     *
     * @return All new announcements. This might be useful, e.g. to notify the user.
     */
    public Collection<Announcement> synchronize(SyncObject object) {

        //Get existing announcements for this course.
        Set<Announcement> existing = new HashSet<>(getAnnouncementsForCourse(object.getCourse(), false));
        Set<Announcement> unread = new HashSet<>(object.getNewObjects());

        List<Announcement> newAnnouncements = new ArrayList<>();

        //*************************************************************
        // Remove stale announcements; these are no longer on the site.
        //*************************************************************

        //Gather the ids of the announcements that should be removed from the database.
        Set<Integer> stale = new HashSet<>();
        for (Announcement a : existing) {
            if (!object.getAllObjects().contains(a)) {
                stale.add(a.getItemId());
            }
        }

        //Remove the stale ids
        remove(stale);

        //*************************************************************************************
        // Synchronize the rest of the announcements; make sure they are present or up to date.
        //*************************************************************************************

        SQLiteDatabase db = helper.getWritableDatabase();
        ZonedDateTime now = ZonedDateTime.now();
        int counter = 0;

        //If new unread announcements for which an email has been sent should count as new.
        final boolean ignoreEmailed = !showEmailed();

        try {
            db.beginTransaction();
            for (Announcement announcement : object.getAllObjects()) {
                //Ensure course is set
                announcement.setCourse(object.getCourse());

                ContentValues contentValues = getValues(announcement);

                //Check if the announcement exists or not - if true, update it
                if (existing.contains(announcement)) {

                    //If the announcement was read online, update the status
                    if(unread.contains(announcement)) {
                        contentValues.remove(AnnouncementTable.COLUMN_READ_DATE); //Don't update the date
                    } else {
                        announcement.setRead(now);
                        contentValues.put(AnnouncementTable.COLUMN_READ_DATE, TtbUtils.serialize(now));
                    }

                    //Do not update ID
                    contentValues.remove(AnnouncementTable.COLUMN_ID); //Don't update the id

                    db.update(
                            AnnouncementTable.TABLE_NAME,
                            contentValues,
                            AnnouncementTable.COLUMN_ID + " = ?",
                            new String[]{String.valueOf(announcement.getItemId())}
                    );
                } else {

                    //If this is not read online or it has an email and is set to email, add the read date.
                    if ((ignoreEmailed && announcement.isEmailSent()) || !unread.contains(announcement)) {
                        announcement.setRead(now);
                        contentValues.put(AnnouncementTable.COLUMN_READ_DATE, TtbUtils.serialize(now));
                    }

                    //If the announcement is unread, add it to the new announcements
                    if (!announcement.isRead()) {
                        newAnnouncements.add(announcement);
                    }

                    db.insertOrThrow(AnnouncementTable.TABLE_NAME, null, contentValues);
                    counter++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.d(TAG, "New announcements for " + object.getCourse().getTitle() + ": " + counter);
        return newAnnouncements;
    }

    /**
     * Remove the announcements with given ID. If an ID is given that is not in the database, the behavior is
     * undefined.
     *
     * @param ids The ids of the announcements to remove.
     *
     * @return The number of rows affected. See {@link SQLiteDatabase#delete(String, String, String[])}.
     */
    public int remove(Collection<Integer> ids) {

        SQLiteDatabase db = helper.getWritableDatabase();

        //Prepare the ids
        String[] converted = new String[ids.size()];
        Integer[] arrayIds = new Integer[ids.size()];
        arrayIds = ids.toArray(arrayIds);

        //Convert ids
        for (int i = 0; i < arrayIds.length; i++) {
            converted[i] = arrayIds[i].toString();
        }

        //Prepare a list of unknowns
        String questions = Utils.commaSeparatedQuestionMarks(ids.size());

        int rows = db.delete(AnnouncementTable.TABLE_NAME, AnnouncementTable.COLUMN_ID + " IN (" + questions + ")", converted);
        Log.d(TAG, "Removed " + rows + " stale announcements.");
        return rows;
    }

    /**
     * @return True if announcements that were emailed should be shown as new or not.
     */
    private boolean showEmailed() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION_EMAIL, MinervaFragment.PREF_DEFAULT_ANNOUNCEMENT_NOTIFICATION_EMAIL);
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
                AnnouncementTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(a.getItemId())}
        );

        Log.i(TAG, "Updated announcement " + a.getItemId());
    }

    /**
     * Update an announcement. This should not be called on the UI thread.
     *
     * @param a The announcement to update.
     */
    public void add(final Announcement a) {
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

        String order = AnnouncementTable.COLUMN_DATE;
        if (reverse) {
            order += " DESC";
        } else {
            order += " ASC";
        }

        Cursor cursor = db.query(
                AnnouncementTable.TABLE_NAME,
                null,
                AnnouncementTable.COLUMN_COURSE + " = ?",
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

    /**
     * Get a list of ids of the announcements for a course in the database.
     *
     * @return List of ids in the database.
     */
    public Map<Course, List<Announcement>> getUnread() {

        SQLiteDatabase db = helper.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        final String courseTable = "course_";

        String announcementJoin = AnnouncementTable.COLUMN_COURSE;
        String courseJoin = courseTable + CourseTable.COLUMN_ID;

        builder.setTables(AnnouncementTable.TABLE_NAME + " INNER JOIN " + CourseTable.TABLE_NAME + " ON " + announcementJoin + "=" + courseJoin);

        Map<Course, List<Announcement>> map = new HashMap<>();

        String[] columns = new String[]{
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_ID,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_TITLE,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_CONTENT,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_EMAIL_SENT,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_STICKY_UNTIL,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_LECTURER,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_DATE,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_READ_DATE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_ID + " AS " + courseTable + CourseTable.COLUMN_ID,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_CODE + " AS " + courseTable + CourseTable.COLUMN_CODE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_TITLE + " AS " + courseTable + CourseTable.COLUMN_TITLE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_DESCRIPTION + " AS " + courseTable + CourseTable.COLUMN_DESCRIPTION,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_TUTOR + " AS " + courseTable + CourseTable.COLUMN_TUTOR,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_STUDENT + " AS " + courseTable + CourseTable.COLUMN_STUDENT,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_ACADEMIC_YEAR + " AS " + courseTable + CourseTable.COLUMN_ACADEMIC_YEAR,
        };

        Cursor c = builder.query(
                db,
                columns,
                AnnouncementTable.COLUMN_READ_DATE + " = ?",
                new String[]{"-1"},
                null,
                null,
                AnnouncementTable.COLUMN_COURSE + " ASC, " + AnnouncementTable.COLUMN_DATE + " DESC"
        );

        if (c == null) {
            return map;
        }

        //Save the course ID separately
        //Get helpers
        CourseExtractor cExtractor = new CourseExtractor.Builder(c)
                .columnId(courseTable + CourseTable.COLUMN_ID)
                .columnCode(courseTable + CourseTable.COLUMN_CODE)
                .columnTitle(courseTable + CourseTable.COLUMN_TITLE)
                .columnDesc(courseTable + CourseTable.COLUMN_DESCRIPTION)
                .columnTutor(courseTable + CourseTable.COLUMN_TUTOR)
                .columnStudent(courseTable + CourseTable.COLUMN_STUDENT)
                .columnYear(courseTable + CourseTable.COLUMN_ACADEMIC_YEAR)
                .build();
        AnnouncementExtractor aExtractor = new AnnouncementExtractor.Builder(c).defaults().build();

        try {
            Course currentCourse = null;
            int counter = 0;
            while (c.moveToNext()) {

                //Get the course id
                String id = c.getString(cExtractor.getColumnId());

                if (currentCourse == null || !currentCourse.getId().equals(id)) {
                    if (currentCourse != null) {
                        Log.d(TAG, "Added " + counter + " announcements for " + currentCourse.getTitle());
                    }
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

        //Sort the announcements by date
//        Comparator<Announcement> comparator = (o1, o2) -> o1.getDate().compareTo(o2.getDate());
//
//        for (List<Announcement> entry : map.values()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                entry.sort(comparator);
//            } else {
//                Collections.sort(entry, comparator);
//            }
//        }

        return map;
    }
}