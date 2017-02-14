package be.ugent.zeus.hydra.minerva.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import be.ugent.zeus.hydra.minerva.course.CourseExtractor;
import be.ugent.zeus.hydra.minerva.course.CourseTable;
import be.ugent.zeus.hydra.minerva.database.Dao;
import be.ugent.zeus.hydra.minerva.database.DiffDao;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.utils.TtbUtils;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.Instant;

import java.util.*;

import static be.ugent.zeus.hydra.minerva.database.Utils.*;

/**
 * The database access object (DAO), to work with Minerva calendar items.
 *
 * @author Niko Strijbol
 */
public class AgendaDao extends Dao implements DiffDao<AgendaItem, Integer> {

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
     * Delete all calendar items that match the given ID's. If the collection is null, everything will be deleted.
     *
     * @param ids The ID's to delete, or null for everything.
     */
    public void delete(Collection<Integer> ids) {

        SQLiteDatabase db = helper.getWritableDatabase();

        if (ids == null) {
            db.delete(AgendaTable.TABLE_NAME, null, null);
        } else if (ids.size() == 1) {
            db.delete(AgendaTable.TABLE_NAME, where(AgendaTable.Columns.ID), args(ids));
        } else {
            db.delete(AgendaTable.TABLE_NAME, in(AgendaTable.Columns.ID, ids.size()), args(ids));
        }
    }

    /**
     * Update existing items. Every column will be replaced with the value from the corresponding object.
     *
     * @param items The items to update.
     */
    public void update(Collection<AgendaItem> items) {

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();

            for (AgendaItem item: items) {
                ContentValues values = getValues(item);
                values.remove(AgendaTable.Columns.ID);
                db.update(AgendaTable.TABLE_NAME, values, where(AgendaTable.Columns.ID), args(item.getItemId()));
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    /**
     * Add new items to the database.
     *
     * @param items The items to insert.
     */
    public void insert(Collection<AgendaItem> items) {
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();
            for (AgendaItem agendaItem: items) {
                db.insertOrThrow(AgendaTable.TABLE_NAME, null, getValues(agendaItem));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static ContentValues getValues(AgendaItem a) {
        ContentValues values = new ContentValues();

        values.put(AgendaTable.Columns.ID, a.getItemId());
        values.put(AgendaTable.Columns.COURSE, a.getCourseId());
        values.put(AgendaTable.Columns.TITLE, a.getTitle());
        values.put(AgendaTable.Columns.CONTENT, a.getContent());
        values.put(AgendaTable.Columns.START_DATE, TtbUtils.serialize(a.getStartDate()));
        values.put(AgendaTable.Columns.END_DATE, TtbUtils.serialize(a.getEndDate()));
        values.put(AgendaTable.Columns.LOCATION, a.getLocation());
        values.put(AgendaTable.Columns.TYPE, a.getType());
        values.put(AgendaTable.Columns.LAST_EDIT_USER, a.getLastEditUser());
        values.put(AgendaTable.Columns.LAST_EDIT, TtbUtils.serialize(a.getLastEdited()));
        values.put(AgendaTable.Columns.LAST_EDIT_TYPE, a.getLastEditType());
        values.put(AgendaTable.Columns.CALENDAR_ID, a.getCalendarId());

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

        String order = AgendaTable.Columns.START_DATE;
        if (reverse) {
            order += " DESC";
        } else {
            order += " ASC";
        }

        Cursor cursor = db.query(
                AgendaTable.TABLE_NAME,
                null,
                AgendaTable.Columns.COURSE + " = ?",
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

    public Stream<AgendaItem> getFutureAgenda(Instant instant) {

        SQLiteDatabase db = helper.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        final String courseTable = "course_";

        String agendaJoin =  AgendaTable.Columns.COURSE;
        String courseJoin = courseTable + CourseTable.Columns.ID;

        builder.setTables(AgendaTable.TABLE_NAME + " INNER JOIN " + CourseTable.TABLE_NAME + " ON " + agendaJoin + "=" + courseJoin);

        String[] columns = new String[]{
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.ID,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.TITLE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.CONTENT,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.START_DATE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.END_DATE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.LOCATION,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.TYPE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.LAST_EDIT_USER,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.LAST_EDIT,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.LAST_EDIT_TYPE,
                AgendaTable.TABLE_NAME + "." + AgendaTable.Columns.CALENDAR_ID,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.ID + " AS " + courseTable + CourseTable.Columns.ID,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.CODE + " AS " + courseTable + CourseTable.Columns.CODE,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.TITLE + " AS " + courseTable + CourseTable.Columns.TITLE,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.DESCRIPTION + " AS " + courseTable + CourseTable.Columns.DESCRIPTION,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.TUTOR + " AS " + courseTable + CourseTable.Columns.TUTOR,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.STUDENT + " AS " + courseTable + CourseTable.Columns.STUDENT,
                CourseTable.TABLE_NAME + "." + CourseTable.Columns.ACADEMIC_YEAR + " AS " + courseTable + CourseTable.Columns.ACADEMIC_YEAR,
        };

        String now = String.valueOf(instant.toEpochMilli());

        Cursor c = builder.query(
                db,
                columns,
                AgendaTable.Columns.START_DATE + " >= ? OR " + AgendaTable.Columns.END_DATE + ">= ?",
                new String[]{now, now},
                null,
                null,
                AgendaTable.Columns.START_DATE + " ASC"
        );

        if (c == null) {
            return RefStreams.empty();
        }

        CourseExtractor cExtractor = new CourseExtractor.Builder(c)
                .columnId(courseTable + CourseTable.Columns.ID)
                .columnCode(courseTable + CourseTable.Columns.CODE)
                .columnTitle(courseTable + CourseTable.Columns.TITLE)
                .columnDesc(courseTable + CourseTable.Columns.DESCRIPTION)
                .columnTutor(courseTable + CourseTable.Columns.TUTOR)
                .columnStudent(courseTable + CourseTable.Columns.STUDENT)
                .columnYear(courseTable + CourseTable.Columns.ACADEMIC_YEAR)
                .build();

        AgendaExtractor aExtractor = new AgendaExtractor.Builder(c).defaults().build();
        List<AgendaItem> result = new ArrayList<>();

        try {
            while (c.moveToNext()) {
                result.add(aExtractor.getAgendaItem(cExtractor.getCourse()));
            }

        } finally {
            c.close();
        }

        return StreamSupport.stream(result);
    }

    public Collection<Integer> getAllIds() {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                AgendaTable.TABLE_NAME,
                new String[]{AgendaTable.Columns.ID},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return Collections.emptySet();
        }

        Set<Integer> ids = new HashSet<>();

        try {
            while (cursor.moveToNext()) {
                ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(AgendaTable.Columns.ID)));
            }
        } finally {
            cursor.close();
        }

        return ids;
    }

    public Collection<Long> getCalendarIdsForIds(Collection<Integer> agendaIds) {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                AgendaTable.TABLE_NAME,
                new String[]{AgendaTable.Columns.CALENDAR_ID},
                in(AgendaTable.Columns.ID, agendaIds.size()),
                args(agendaIds),
                null,
                null,
                null
        );

        if (cursor == null) {
            return Collections.emptyList();
        }

        Set<Long> result = new HashSet<>();

        try {
            while(cursor.moveToNext()) {
                result.add(cursor.getLong(cursor.getColumnIndexOrThrow(AgendaTable.Columns.CALENDAR_ID)));
            }
        } finally {
            cursor.close();
        }

        return result;
    }
}