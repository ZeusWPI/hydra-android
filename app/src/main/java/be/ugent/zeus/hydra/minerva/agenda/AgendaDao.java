package be.ugent.zeus.hydra.minerva.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import be.ugent.zeus.hydra.minerva.database.Dao;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.utils.TtbUtils;

import java.util.*;

/**
 * Dao to access announcements from the database.
 *
 * @author Niko Strijbol
 */
public class AgendaDao extends Dao {

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
        if(reverse) {
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

        if(cursor == null) {
            return result;
        }

        try {
            int columnIndex = cursor.getColumnIndex(AgendaTable.COLUMN_ID);
            int columnTitle = cursor.getColumnIndex(AgendaTable.COLUMN_TITLE);
            int columnContent = cursor.getColumnIndex(AgendaTable.COLUMN_CONTENT);
            int columnStartDate = cursor.getColumnIndex(AgendaTable.COLUMN_START_DATE);
            int columnEndDate = cursor.getColumnIndex(AgendaTable.COLUMN_END_DATE);
            int columnLocation = cursor.getColumnIndex(AgendaTable.COLUMN_LOCATION);
            int columnType = cursor.getColumnIndex(AgendaTable.COLUMN_TYPE);
            int columnLastEditUser = cursor.getColumnIndex(AgendaTable.COLUMN_LAST_EDIT_USER);
            int columnLastEdit = cursor.getColumnIndex(AgendaTable.COLUMN_LAST_EDIT);
            int columnLastEditType = cursor.getColumnIndex(AgendaTable.COLUMN_LAST_EDIT_TYPE);

            while (cursor.moveToNext()) {
                AgendaItem a = new AgendaItem();
                a.setCourse(course);
                a.setItemId(cursor.getInt(columnIndex));
                a.setTitle(cursor.getString(columnTitle));
                a.setContent(cursor.getString(columnContent));
                a.setStartDate(TtbUtils.unserialize(cursor.getLong(columnStartDate)));
                a.setEndDate(TtbUtils.unserialize(cursor.getLong(columnEndDate)));
                a.setLocation(cursor.getString(columnLocation));
                a.setType(cursor.getString(columnType));
                a.setLastEditUser(cursor.getString(columnLastEditUser));
                a.setLastEdited(TtbUtils.unserialize(cursor.getLong(columnLastEdit)));
                a.setLastEditType(cursor.getString(columnLastEditType));
                result.add(a);
            }
        } finally {
            cursor.close();
        }

        return result;
    }
}