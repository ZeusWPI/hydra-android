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
     * Delete all data.
     */
    public void deleteAll() {
        helper.getWritableDatabase().delete(AnnouncementTable.TABLE_NAME, null, null);
    }

    /**
     * Synchronise announcements for one course.
     *
     * @param announcements The announcements.
     * @param first If this is the first sync or not.
     *
     * @return The new announcements.
     */
    public List<Announcement> synchronisePartial(Collection<Announcement> announcements, Course course, boolean first, Context context) {

        //Get existing announcements.
        Set<Integer> present = getIdsForCourse(course);
        List<Announcement> newAnnouncements = new ArrayList<>();

        SQLiteDatabase db = helper.getWritableDatabase();

        int counter = 0;
        try {
            db.beginTransaction();

            //Delete old announcements
            Collection<Integer> idCollection = getRemovable(present, announcements);
            Integer[] ids = new Integer[idCollection.size()];
            ids = idCollection.toArray(ids);
            String questions = Utils.commaSeparatedQuestionMarks(idCollection.size());

            String[] converted = new String[ids.length];
            //Convert ids
            for(int i = 0; i < ids.length; i++) {
                converted[i] = ids[i].toString();
            }

            int rows = db.delete(AnnouncementTable.TABLE_NAME, AnnouncementTable.COLUMN_ID + " IN (" + questions + ")", converted);
            Log.d(TAG, "Removed " + rows + " stale announcements.");

            //If we are doing the first sync, we want to set everything to read.
            ZonedDateTime now = ZonedDateTime.now();

            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            final boolean showEmail = pref.getBoolean(MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION_EMAIL, MinervaFragment.PREF_DEFAULT_ANNOUNCEMENT_NOTIFICATION_EMAIL);

            for (Announcement announcement: announcements) {
                //Make sure the course is set
                announcement.setCourse(course);

                ContentValues value = getValues(announcement);

                //Update the announcement if it is present
                if(present.contains(announcement.getItemId())) {
                    value.remove(AnnouncementTable.COLUMN_ID); //Don't update the id
                    value.remove(AnnouncementTable.COLUMN_READ_DATE); //Don't update the date
                    db.update(
                            AnnouncementTable.TABLE_NAME,
                            value,
                            AnnouncementTable.COLUMN_ID + " = ?",
                            new String[]{String.valueOf(announcement.getItemId())}
                            );
                } else {
                    //If this is the first sync or it has an email and is set to email, add the read date.
                    if(first || (!showEmail && announcement.isEmailSent())) {
                        announcement.setRead(now);
                        value.put(AnnouncementTable.COLUMN_READ_DATE, TtbUtils.serialize(now));
                    }

                    //If the announcement is unread, add it to the new announcements
                    if(!announcement.isRead()) {
                        newAnnouncements.add(announcement);
                    }

                    db.insertOrThrow(AnnouncementTable.TABLE_NAME, null, value);
                    counter++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.d(TAG, "New announcements for " + course.getTitle() + ": " + counter);
        return newAnnouncements;
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
        values.put(AnnouncementTable.COLUMN_EMAIL_SENT, boolToInt(a.isEmailSent()));
        values.put(AnnouncementTable.COLUMN_STICKY_UNTIL, 0);
        values.put(AnnouncementTable.COLUMN_LECTURER, a.getLecturer());
        values.put(AnnouncementTable.COLUMN_DATE, TtbUtils.serialize(a.getDate()));
        values.put(AnnouncementTable.COLUMN_READ_DATE, a.isRead() ? TtbUtils.serialize(a.getDate()) : -1);

        return values;
    }

    /**
     * Set the announcement as read if that is not the case already.
     *
     * @param a The announcement
     */
    public void update(Announcement a) {

        Log.i(TAG, "Updating announcement " + a.getItemId());

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = getValues(a);
        db.update(
                AnnouncementTable.TABLE_NAME,
                values,
                AnnouncementTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(a.getItemId())}
                );
    }

    private static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    /**
     * Get the list of ids that are not in the given {@code announcements}. The default usage for this is the find
     * stale announcements that are no longer on Minerva, but still in our database.
     *
     * This method runs in linear time: O(n), with n = size(courses).
     *
     * @param ids Ids of announcements in the database.
     * @param announcements Announcements from the Minerva servers.
     *
     * @return Local courses that can be deleted.
     */
    private static Set<Integer> getRemovable(final Set<Integer> ids, final Iterable<Announcement> announcements) {
        /*
        * We copy the set of ids because we modify it. Logically we would iterate the ids and check
        * if each id is present in the announcements. Because of how the Java collections work, we can't do that without
        * iterating the whole collection for every id. If size(ids) = n and size(announcements) = m, this would result
        * in a complexity of O(n^m).
        *
        * Instead we iterate the announcements, which is O(m). We check if the set of ids contains it, O(1), and if it
        * does, we remove it from the set O(1). The result is thus O(m).
        */
        Set<Integer> removable = new HashSet<>(ids);
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
     * @param course The course.
     *
     * @return List of ids in the database.
     */
    private Set<Integer> getIdsForCourse(Course course) {

        SQLiteDatabase db = helper.getReadableDatabase();
        Set<Integer> result = new HashSet<>();

        Cursor cursor = db.query(
                AnnouncementTable.TABLE_NAME,
                new String[] {AnnouncementTable.COLUMN_ID},
                AnnouncementTable.COLUMN_COURSE + " = ?",
                new String[]{course.getId()},
                null, null, null);

        if(cursor == null) {
            return result;
        }

        try {
            int columnIndex = cursor.getColumnIndex(AnnouncementTable.COLUMN_ID);

            while (cursor.moveToNext()) {
                result.add(cursor.getInt(columnIndex));
            }
        } finally {
            cursor.close();
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

        Cursor cursor = db.query(
                AnnouncementTable.TABLE_NAME,
                null,
                AnnouncementTable.COLUMN_COURSE + " = ?",
                new String[]{course.getId()},
                null, null, order);

        if(cursor == null) {
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

        String announcementJoin =  AnnouncementTable.COLUMN_COURSE;
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
                AnnouncementTable.COLUMN_COURSE + " ASC"
        );

        if(c == null) {
            return map;
        }

        Log.d(TAG, Arrays.toString(c.getColumnNames()));

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

                if(currentCourse == null || !currentCourse.getId().equals(id)) {
                    if(currentCourse != null) {
                        Log.d(TAG, "Added " + counter + " announcements for " + currentCourse.getTitle());
                    }
                    //Add the course
                    currentCourse = cExtractor.getCourse();
                    map.put(currentCourse, new ArrayList<Announcement>());
                    counter = 0;
                }

                map.get(currentCourse).add(aExtractor.getAnnouncement(currentCourse));
                counter++;
            }

        } finally {
            c.close();
        }

        //Sort the announcements by date
        Comparator<Announcement> comparator = new Comparator<Announcement>() {
            @Override
            public int compare(Announcement o1, Announcement o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };

        for (List<Announcement> entry : map.values()){
            Collections.sort(entry, comparator);
        }

        return map;
    }
}