package be.ugent.zeus.hydra.data.database.minerva;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.data.database.Dao;
import be.ugent.zeus.hydra.data.database.DiffDao;
import be.ugent.zeus.hydra.data.database.Utils;
import be.ugent.zeus.hydra.data.models.minerva.Course;

import java.util.*;

import static be.ugent.zeus.hydra.data.database.Utils.*;

/**
 * This class provides easy access to the {@link Course}s in the database.
 *
 * {@inheritDoc}
 *
 * @author Niko Strijbol
 */
public class CourseDao extends Dao implements DiffDao<Course, String> {

    private static final String TAG = "CourseDao";

    /**
     * @param context The application context.
     */
    public CourseDao(Context context) {
        super(context);
    }

    public void insert(Course course) {
        insert(Collections.singleton(course));
    }

    /**
     * Add new courses to the database. This method assumes the courses are not in the database already. If they are,
     * you should remove them first, or update them instead.
     *
     * @param courses The courses to insert.
     */
    public void insert(Collection<Course> courses) {

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();
            for (Course course : courses) {
                db.insertOrThrow(CourseTable.TABLE_NAME, null, getValues(course));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Synchronise a collection of courses with the database. The end result is that the database will only contain
     * the provided courses. Data that is not present in the courses will be retained.
     *
     * @deprecated This method does not belong in a dao.
     *
     * @param courses The courses to insert.
     */
    @Deprecated
    public void synchronise(Collection<Course> courses) throws SQLException {

        //Get existing courses.
        Set<String> present = getIds();

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();

            //Delete old courses
            Collection<String> idCollection = getRemovable(present, courses);
            String[] ids = new String[idCollection.size()];
            ids = idCollection.toArray(ids);
            String questions = Utils.commaSeparatedQuestionMarks(idCollection.size());

            int rows = db.delete(CourseTable.TABLE_NAME, CourseTable.Columns.ID + " IN (" + questions + ")", ids);
            Log.d(TAG, "Removed " + rows + " stale courses.");

            for (Course course: courses ) {

                //Get the values
                ContentValues value = getValues(course);

                //If the course is present, we update it's rows, otherwise we insert it to the database.
                if(present.contains(course.getId())) {
                    value.remove(CourseTable.Columns.ID); //We don't need to set this
                    db.update(CourseTable.TABLE_NAME, value, CourseTable.Columns.ID + " = ?", new String[]{course.getId()});
                }
                //Add new course
                else {
                    db.insertOrThrow(CourseTable.TABLE_NAME, null, value);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Delete all calendar items that match the given ID's. If the collection is null, everything will be deleted.
     *
     * @param ids The ID's to delete, or null for everything.
     */
    public void delete(Collection<String> ids) {

        SQLiteDatabase db = helper.getWritableDatabase();

        if (ids == null) {
            db.delete(CourseTable.TABLE_NAME, null, null);
        } else if (ids.size() == 1) {
            db.delete(CourseTable.TABLE_NAME, where(CourseTable.Columns.ID), args(ids));
        } else {
            db.delete(CourseTable.TABLE_NAME, in(CourseTable.Columns.ID, ids.size()), args(ids));
        }
    }

    /**
     * Delete all courses from the database.
     */
    public void deleteAll() {
        helper.getWritableDatabase().delete(CourseTable.TABLE_NAME, null, null);
    }

    /**
     * Update existing items. Every column will be replaced with the value from the corresponding object.
     *
     * @param items The items to update.
     */
    public void update(Collection<Course> items) {

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (Course item: items) {
                ContentValues values = getValues(item);
                values.remove(CourseTable.Columns.ID);
                db.update(CourseTable.TABLE_NAME, values, where(CourseTable.Columns.ID), args(item.getId()));
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    /**
     * Get values for a course.
     *
     * @param course The course.
     *
     * @return The values.
     */
    private static ContentValues getValues(Course course) {
        ContentValues values = new ContentValues();

        values.put(CourseTable.Columns.ID, course.getId());
        values.put(CourseTable.Columns.CODE, course.getCode());
        values.put(CourseTable.Columns.TITLE, course.getTitle());
        values.put(CourseTable.Columns.DESCRIPTION, course.getDescription());
        values.put(CourseTable.Columns.TUTOR, course.getTutorName());
        values.put(CourseTable.Columns.STUDENT, course.getStudent());
        values.put(CourseTable.Columns.ACADEMIC_YEAR, course.getAcademicYear());

        return values;
    }

    /**
     * Get a list of ids of the courses in the database.
     *
     * @return List of ids in the database.
     */
    @NonNull
    public Set<String> getIds() {

        SQLiteDatabase db = helper.getReadableDatabase();
        Set<String> result = new HashSet<>();

        Cursor cursor = db.query(CourseTable.TABLE_NAME, new String[]{CourseTable.Columns.ID}, null, null, null, null, null);

        if (cursor == null) {
            return result;
        }

        try {
            int columnIndex = cursor.getColumnIndex(CourseTable.Columns.ID);
            while (cursor.moveToNext()) {
                result.add(cursor.getString(columnIndex));
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    /**
     * Get the list of ids that are not in the given {@code courses}. The default usage for this is the find stale
     * courses that are no longer on Minerva, but still in our database.
     *
     * This method runs in linear time: O(n), with n = size(courses).
     *
     * @param ids Ids of courses in the database.
     * @param courses Courses from the Minerva servers.
     *
     * @return Local courses that can be deleted.
     */
    private static Set<String> getRemovable(final Set<String> ids, final Collection<Course> courses) {
       /*
        * We copy the set of ids because we modify it. Logically we would iterate the ids and check
        * if each id is present in the courses. Because of how the Java collections work, we can't do that without
        * iterating the whole collection for every id. If size(ids) = n and size(courses) = m, this would result
        * in a complexity of O(n^m).
        *
        * Instead we iterate the courses, which is O(m). We check if the set of ids contains it, O(1), and if it
        * does, we remove it from the set O(1). The result is thus O(m).
        */
        Set<String> removable = new HashSet<>(ids);

        for (Course course: courses) {
            if(removable.contains(course.getId())) {
                removable.remove(course.getId());
            }
        }

        return removable;
    }

    /**
     * Get all courses that are currently in the database.
     *
     * @return A list of all courses.
     */
    @NonNull
    public List<Course> getAll() {
        Log.d(TAG, "Getting all courses");

        SQLiteDatabase db = helper.getReadableDatabase();
        List<Course> result = new ArrayList<>();

        //Get the cursor.
        Cursor c = db.query(
                CourseTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                CourseTable.Columns.TITLE);

        //If the cursor is null, abort
        if (c == null) {
            return result;
        }

        //Get a helper.
        CourseExtractor extractor = new CourseExtractor.Builder(c).defaults().build();

        //Get the actual r
        try {
            while (c.moveToNext()) {
                result.add(extractor.getCourse());
            }
        } finally {
            c.close();
        }

        return result;
    }
}