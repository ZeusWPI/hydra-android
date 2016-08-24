package be.ugent.zeus.hydra.minerva.announcement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import be.ugent.zeus.hydra.minerva.course.CourseTable;
import be.ugent.zeus.hydra.minerva.database.DatabaseHelper;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.*;

/**
 * Dao to access announcements from the database.
 *
 * @author Niko Strijbol
 */
public class AnnouncementDao {

    private static final String TAG = "AnnouncementDao";

    private final DatabaseHelper helper;

    /**
     * @param context The application context.
     */
    public AnnouncementDao(Context context) {
        this.helper = DatabaseHelper.getInstance(context);
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

            Date date = null;
            if(first) {
                date = new Date();
            }

            for (Announcement announcement: announcements ) {

                announcement.setRead(date);
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
                    if(!first) {
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
        values.put(AnnouncementTable.COLUMN_READ_DATE, a.isRead() ? a.getDate().getTime() : 0);

        return values;
    }

    /**
     * Set the announcement as read if that is not the case already.
     *
     * @param a
     */
    public void update(Announcement a) {

        Log.i(TAG, "Updating announcement " + a.getItemId());

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            ContentValues values = getValues(a);
            db.update(
                    AnnouncementTable.TABLE_NAME,
                    values,
                    AnnouncementTable.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(a.getItemId())}
                    );
        } finally {
            db.close();
        }
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
                    result = getAnnouncements(cursor, course);
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
     * todo: can we abstract some cursor stuff away or not?
     *
     * @param reverse If the announcements should be reversed (newest first) or not.
     *
     * @return List of ids in the database.
     */
    public Map<Course, List<Announcement>> getUnread(boolean reverse) {

        SQLiteDatabase db = helper.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        final String courseTable = "course_";

        String announcementJoin =  AnnouncementTable.COLUMN_COURSE;
        String courseJoin = courseTable + CourseTable.COLUMN_ID;

        builder.setTables(
                AnnouncementTable.TABLE_NAME +
                        " INNER JOIN " + CourseTable.TABLE_NAME + " ON " + announcementJoin + "=" + courseJoin);

        String order = AnnouncementTable.COLUMN_DATE;
        if(reverse) {
            order += " DESC";
        } else {
            order += " ASC";
        }

        Map<Course, List<Announcement>> map = new HashMap<>();

        String[] columns = new String[]{
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_ID,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_TITLE,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_CONTENT,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_EMAIL_SENT,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_STICKY_UNTIL,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_LECTURER,
                AnnouncementTable.TABLE_NAME + "." + AnnouncementTable.COLUMN_DATE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_ID + " AS " + courseTable + CourseTable.COLUMN_ID,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_CODE + " AS " + courseTable + CourseTable.COLUMN_CODE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_TITLE + " AS " + courseTable + CourseTable.COLUMN_TITLE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_DESCRIPTION + " AS " + courseTable + CourseTable.COLUMN_DESCRIPTION,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_TUTOR + " AS " + courseTable + CourseTable.COLUMN_TUTOR,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_STUDENT + " AS " + courseTable + CourseTable.COLUMN_STUDENT,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_ACADEMIC_YEAR + " AS " + courseTable + CourseTable.COLUMN_ACADEMIC_YEAR,
        };

        try {
            Cursor c = builder.query(
                    db,
                    columns,
                    AnnouncementTable.COLUMN_READ_DATE + " = ?",
                    new String[]{"0"},
                    null, null, order);

            if (c != null) {

                Log.d(TAG, Arrays.toString(c.getColumnNames()));

                //Course columns
                int cColumnId = c.getColumnIndexOrThrow(courseTable + CourseTable.COLUMN_ID);
                int cColumnCode = c.getColumnIndexOrThrow(courseTable + CourseTable.COLUMN_CODE);
                int cColumnTitle = c.getColumnIndexOrThrow(courseTable + CourseTable.COLUMN_TITLE);
                int cColumnDesc = c.getColumnIndexOrThrow(courseTable + CourseTable.COLUMN_DESCRIPTION);
                int cColumnTutor = c.getColumnIndexOrThrow(courseTable + CourseTable.COLUMN_TUTOR);
                int cColumnStudent = c.getColumnIndexOrThrow(courseTable + CourseTable.COLUMN_STUDENT);
                int cColumnYear = c.getColumnIndexOrThrow(courseTable + CourseTable.COLUMN_ACADEMIC_YEAR);

                //Announcement columns
                int aColumnIndex = c.getColumnIndexOrThrow(AnnouncementTable.COLUMN_ID);
                int aColumnTitle = c.getColumnIndexOrThrow(AnnouncementTable.COLUMN_TITLE);
                int aColumnContent = c.getColumnIndexOrThrow(AnnouncementTable.COLUMN_CONTENT);
                int aColumnEmailSent = c.getColumnIndexOrThrow(AnnouncementTable.COLUMN_EMAIL_SENT);
                int aColumnSticky = c.getColumnIndexOrThrow(AnnouncementTable.COLUMN_STICKY_UNTIL);
                int aColumnLecturer = c.getColumnIndexOrThrow(AnnouncementTable.COLUMN_LECTURER);
                int aColumnDate = c.getColumnIndexOrThrow(AnnouncementTable.COLUMN_DATE);

                try {

                    Course currentCourse = null;
                    while (c.moveToNext()) {

                        //Get the course id
                        String id = c.getString(cColumnId);

                        if(currentCourse == null || !currentCourse.getId().equals(id)) {
                            //Add the course
                            currentCourse = new Course();
                            currentCourse.setId(c.getString(cColumnId));
                            currentCourse.setCode(c.getString(cColumnCode));
                            currentCourse.setTitle(c.getString(cColumnTitle));
                            currentCourse.setDescription(c.getString(cColumnDesc));
                            currentCourse.setTutorName(c.getString(cColumnTutor));
                            currentCourse.setStudent(c.getString(cColumnStudent));
                            currentCourse.setAcademicYear(c.getInt(cColumnYear));
                            map.put(currentCourse, new ArrayList<Announcement>());
                        }

                        //Get the announcement
                        Announcement a = new Announcement();
                        a.setCourse(currentCourse);
                        a.setItemId(c.getInt(aColumnIndex));
                        a.setTitle(c.getString(aColumnTitle));
                        a.setContent(c.getString(aColumnContent));
                        a.setEmailSent(intToBool(c.getInt(aColumnEmailSent)));
                        a.setLecturer(c.getString(aColumnLecturer));
                        a.setDate(new Date(c.getLong(aColumnDate)));

                        map.get(currentCourse).add(a);
                    }

                } finally {
                    c.close();
                }
            }
        } finally {
            db.close();
        }

        return map;
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

    private List<Announcement> getAnnouncements(@NonNull Cursor cursor, Course course) {

        List<Announcement> result = new ArrayList<>();

        int columnIndex = cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_ID);
        int columnTitle = cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_TITLE);
        int columnContent = cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_CONTENT);
        int columnEmailSent = cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_EMAIL_SENT);
        int columnSticky = cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_STICKY_UNTIL);
        int columnLecturer = cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_LECTURER);
        int columnDate = cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_DATE);
        int columnRead = cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_READ_DATE);

        while (cursor.moveToNext()) {
            Announcement a = new Announcement();
            a.setCourse(course);
            a.setItemId(cursor.getInt(columnIndex));
            a.setTitle(cursor.getString(columnTitle));
            a.setContent(cursor.getString(columnContent));
            a.setEmailSent(intToBool(cursor.getInt(columnEmailSent)));
            a.setLecturer(cursor.getString(columnLecturer));
            a.setDate(new Date(cursor.getLong(columnDate)));
            long d = cursor.getLong(columnRead);
            if(d != 0) {
                a.setRead(new Date(d));
            }
            result.add(a);
        }

        return result;
    }
}