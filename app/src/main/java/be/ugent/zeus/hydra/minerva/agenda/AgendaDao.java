package be.ugent.zeus.hydra.minerva.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import be.ugent.zeus.hydra.minerva.course.CourseExtractor;
import be.ugent.zeus.hydra.minerva.course.CourseTable;
import be.ugent.zeus.hydra.minerva.database.Dao;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.utils.TtbUtils;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Dao to access announcements from the database.
 *
 * @author Niko Strijbol
 */
public class AgendaDao extends Dao {

    private final static String TAG = "AgendaDao";

    /**
     * @param context The application context.
     */
    public AgendaDao(Context context) {
        super(context);
    }

    /**
     * Delete all data.
     */
    public void deleteAll() {
        helper.getWritableDatabase().delete(AgendaTable.TABLE_NAME, null, null);
    }

    /**
     * Delete all agenda items, and add the given ones.
     *
     * @param agenda The items to add.
     */
    public void replace(Collection<AgendaItem> agenda) {

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();

            //Clear all agenda items for this course.
            db.delete(AgendaTable.TABLE_NAME, null, null);

            for (AgendaItem agendaItem: agenda ) {
                ContentValues value = getValues(agendaItem);
                db.insertOrThrow(AgendaTable.TABLE_NAME, null, value);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static ContentValues getValues(AgendaItem a) {
        ContentValues values = new ContentValues();

        values.put(AgendaTable.COLUMN_ID, a.getItemId());
        values.put(AgendaTable.COLUMN_COURSE, a.getCourseId());
        values.put(AgendaTable.COLUMN_TITLE, a.getTitle());
        values.put(AgendaTable.COLUMN_CONTENT, a.getContent());
        values.put(AgendaTable.COLUMN_START_DATE, TtbUtils.serialize(a.getStartDate()));
        values.put(AgendaTable.COLUMN_END_DATE, TtbUtils.serialize(a.getEndDate()));
        values.put(AgendaTable.COLUMN_LOCATION, a.getLocation());
        values.put(AgendaTable.COLUMN_TYPE, a.getType());
        values.put(AgendaTable.COLUMN_LAST_EDIT_USER, a.getLastEditUser());
        values.put(AgendaTable.COLUMN_LAST_EDIT, TtbUtils.serialize(a.getLastEdited()));
        values.put(AgendaTable.COLUMN_LAST_EDIT_TYPE, a.getLastEditType());

        return values;
    }

    /**
     * Get a list of ids of the agenda items for a course in the database.
     *
     * @param course The course.
     * @param reverse If the agenda items should be reversed (newest first) or not.
     *
     * @return List of ids in the database.
     */
    public List<AgendaItem> getAgendaForCourse(Course course, boolean reverse) {

        SQLiteDatabase db = helper.getReadableDatabase();
        List<AgendaItem> result = new ArrayList<>();

        String order = AgendaTable.COLUMN_START_DATE;
        if (reverse) {
            order += " DESC";
        } else {
            order += " ASC";
        }

        Cursor cursor = db.query(
                AgendaTable.TABLE_NAME,
                null,
                AgendaTable.COLUMN_COURSE + " = ?",
                new String[]{course.getId()},
                null, null, order);

        if (cursor == null) {
            return result;
        }

        try {
            AgendaExtractor extractor = new AgendaExtractor.Builder(cursor).defaults().build();

            while (cursor.moveToNext()) {
                result.add(extractor.getAgendaItem(course));
            }

        } finally {
            cursor.close();
        }

        return result;
    }

    public List<AgendaItem> getFutureAgenda(Instant instant) {

        SQLiteDatabase db = helper.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        final String courseTable = "course_";

        String agendaJoin =  AgendaTable.COLUMN_COURSE;
        String courseJoin = courseTable + CourseTable.COLUMN_ID;

        builder.setTables(AgendaTable.TABLE_NAME + " INNER JOIN " + CourseTable.TABLE_NAME + " ON " + agendaJoin + "=" + courseJoin);

        List<AgendaItem> result = new ArrayList<>();

        String[] columns = new String[]{
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_ID,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_TITLE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_CONTENT,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_START_DATE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_END_DATE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_LOCATION,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_TYPE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_LAST_EDIT_USER,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_LAST_EDIT,
                AgendaTable.TABLE_NAME + "." + AgendaTable.COLUMN_LAST_EDIT_TYPE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_ID + " AS " + courseTable + CourseTable.COLUMN_ID,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_CODE + " AS " + courseTable + CourseTable.COLUMN_CODE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_TITLE + " AS " + courseTable + CourseTable.COLUMN_TITLE,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_DESCRIPTION + " AS " + courseTable + CourseTable.COLUMN_DESCRIPTION,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_TUTOR + " AS " + courseTable + CourseTable.COLUMN_TUTOR,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_STUDENT + " AS " + courseTable + CourseTable.COLUMN_STUDENT,
                CourseTable.TABLE_NAME + "." + CourseTable.COLUMN_ACADEMIC_YEAR + " AS " + courseTable + CourseTable.COLUMN_ACADEMIC_YEAR,
        };

        String now = String.valueOf(instant.toEpochMilli());

        Cursor c = builder.query(
                db,
                columns,
                AgendaTable.COLUMN_START_DATE + " >= ? OR " + AgendaTable.COLUMN_END_DATE + ">= ?",
                new String[]{now, now},
                null,
                null,
                AgendaTable.COLUMN_START_DATE + " ASC"
        );

        if(c == null) {
            return result;
        }

        CourseExtractor cExtractor = new CourseExtractor.Builder(c)
                .columnId(courseTable + CourseTable.COLUMN_ID)
                .columnCode(courseTable + CourseTable.COLUMN_CODE)
                .columnTitle(courseTable + CourseTable.COLUMN_TITLE)
                .columnDesc(courseTable + CourseTable.COLUMN_DESCRIPTION)
                .columnTutor(courseTable + CourseTable.COLUMN_TUTOR)
                .columnStudent(courseTable + CourseTable.COLUMN_STUDENT)
                .columnYear(courseTable + CourseTable.COLUMN_ACADEMIC_YEAR)
                .build();

        AgendaExtractor aExtractor = new AgendaExtractor.Builder(c).defaults().build();

        try {
            while (c.moveToNext()) {
                result.add(aExtractor.getAgendaItem(cExtractor.getCourse()));
            }

        } finally {
            c.close();
        }

        return result;
    }
}