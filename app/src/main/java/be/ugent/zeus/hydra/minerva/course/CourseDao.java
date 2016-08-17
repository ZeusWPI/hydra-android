package be.ugent.zeus.hydra.minerva.course;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import be.ugent.zeus.hydra.minerva.database.DatabaseHelper;
import be.ugent.zeus.hydra.minerva.database.Dao;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.*;

/**
 * @author Niko Strijbol
 * @version 16/08/2016
 */
public class CourseDao implements Dao<Course> {

    private static final String TAG = "CourseDao";

    private DatabaseHelper helper;

    /**
     * @param context The application context.
     */
    public CourseDao(Context context) {
        this.helper = DatabaseHelper.getInstance(context);
    }

    public void add(Course course) {
        add(Collections.singleton(course));
    }

    /**
     * Add new courses to the database. This method assumes the courses are not in the database
     * already. If they are, you should remove them first, or update them instead.
     *
     * @param courses The courses to add.
     */
    public void add(Collection<Course> courses) {

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();
            for (Course course : courses) {
                db.insertOrThrow(CourseTable.TABLE_NAME, null, getValues(course));
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, "Error while inserting.", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    /**
     * Synchronise a collection of courses with the database. The end result is that the database will only contain
     * the provided courses. Data that is not present in the courses will be retained.
     *
     * Note: the implementation of this method is not guaranteed. This means that the method may re-use rows, but can
     * also decide to discard everything.
     *
     * @param courses The courses to add.
     */
    public void synchronise(Collection<Course> courses) throws SQLException {

        SQLiteDatabase db = helper.getWritableDatabase();

        //Get existing courses.
        Set<String> present = getIds(db);

        try {
            db.beginTransaction();

            //Delete old courses
            String ids = TextUtils.join(", ", getRemovable(present, courses));
            db.delete(CourseTable.TABLE_NAME, CourseTable.COLUMN_ID + " IN (?)", new String[]{ids});

            for (Course course: courses ) {

                ContentValues value = getValues(course);

                //Update the course
                if(present.contains(course.getId())) {
                    value.remove(CourseTable.COLUMN_ID);
                    db.update(CourseTable.TABLE_NAME, value, CourseTable.COLUMN_ID + " = ?", new String[]{course.getId()});
                }
                //Add new course
                else {
                    db.insertOrThrow(CourseTable.TABLE_NAME, null, value);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(CourseTable.TABLE_NAME, null, null);
        } finally {
            db.close();
        }
    }

    private static ContentValues getValues(Course course) {
        ContentValues values = new ContentValues();

        values.put(CourseTable.COLUMN_ID, course.getId());
        values.put(CourseTable.COLUMN_CODE, course.getCode());
        values.put(CourseTable.COLUMN_TITLE, course.getTitle());
        values.put(CourseTable.COLUMN_DESCRIPTION, course.getDescription());
        values.put(CourseTable.COLUMN_TUTOR, course.getTutorName());
        values.put(CourseTable.COLUMN_STUDENT, course.getStudent());
        values.put(CourseTable.COLUMN_ACADEMIC_YEAR, course.getAcademicYear());

        return values;
    }

    private static Course getCourse(Cursor c) {
        Course course = new Course();
        course.setId(c.getString(c.getColumnIndex(CourseTable.COLUMN_ID)));
        course.setCode(c.getString(c.getColumnIndex(CourseTable.COLUMN_CODE)));
        course.setTitle(c.getString(c.getColumnIndex(CourseTable.COLUMN_TITLE)));
        course.setDescription(c.getString(c.getColumnIndex(CourseTable.COLUMN_DESCRIPTION)));
        course.setTutorName(c.getString(c.getColumnIndex(CourseTable.COLUMN_TUTOR)));
        course.setStudent(c.getString(c.getColumnIndex(CourseTable.COLUMN_STUDENT)));
        course.setAcademicYear(c.getInt(c.getColumnIndex(CourseTable.COLUMN_ACADEMIC_YEAR)));
        return course;
    }

    /**
     * Get a list of ids of the courses in the database.
     *
     * @param db The database.
     *
     * @return List of ids in the database.
     */
    private static Set<String> getIds(SQLiteDatabase db) {

        Cursor cursor = db.query(CourseTable.TABLE_NAME, new String[] {CourseTable.COLUMN_ID}, null, null, null, null, null);

        Set<String> result = new HashSet<>();

        if(cursor != null) {
            try {
                int columnIndex = cursor.getColumnIndex(CourseTable.COLUMN_ID);

                while (cursor.moveToNext()) {
                    result.add(cursor.getString(columnIndex));
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }

    /**
     * A set of ids that are not in the course.
     *
     * @param ids Ids of local courses.
     * @param courses Remote courses.
     * @return Local courses that can be deleted.
     */
    private static Set<String> getRemovable(final Set<String> ids, final Collection<Course> courses) {
        Set<String> removable = new HashSet<>(ids);
        //Iterate the course to prevent O(n^2)
        for (Course course: courses) {
            if(removable.contains(course.getId())) {
                removable.remove(course.getId());
            }
        }

        return removable;
    }

    @NonNull
    @Override
    public List<Course> getAll() {
        Log.d(TAG, "Getting all courses");
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query(CourseTable.TABLE_NAME, null, null, null, null, null, null);

        List<Course> result = new ArrayList<>();

        if(c != null) {
            try {
                int columnId = c.getColumnIndex(CourseTable.COLUMN_ID);
                int columnCode = c.getColumnIndex(CourseTable.COLUMN_CODE);
                int columnTitle = c.getColumnIndex(CourseTable.COLUMN_TITLE);
                int columnDesc = c.getColumnIndex(CourseTable.COLUMN_DESCRIPTION);
                int columnTutor = c.getColumnIndex(CourseTable.COLUMN_TUTOR);
                int columnStudent = c.getColumnIndex(CourseTable.COLUMN_STUDENT);
                int columnYear = c.getColumnIndex(CourseTable.COLUMN_ACADEMIC_YEAR);

                while (c.moveToNext()) {
                    Course course = new Course();
                    course.setId(c.getString(columnId));
                    course.setCode(c.getString(columnCode));
                    course.setTitle(c.getString(columnTitle));
                    course.setDescription(c.getString(columnDesc));
                    course.setTutorName(c.getString(columnTutor));
                    course.setStudent(c.getString(columnStudent));
                    course.setAcademicYear(c.getInt(columnYear));
                    result.add(course);
                }
            } finally {
                c.close();
                db.close();
            }
        }

        return result;
    }
}