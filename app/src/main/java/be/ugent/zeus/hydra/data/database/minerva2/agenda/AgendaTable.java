package be.ugent.zeus.hydra.data.database.minerva2.agenda;

import android.provider.BaseColumns;

/**
 * Contract for the table with the agenda items. This represents the current table and column names. This might change,
 * so you CANNOT use these in migrations.
 *
 * @author Niko Strijbol
 */
public final class AgendaTable implements BaseColumns {

    public static final String TABLE_NAME = "minerva_calendar";

    public interface Columns extends BaseColumns {
        //Columns for the data
        String ID = _ID;
        String COURSE = "course";
        String TITLE = "title";
        String CONTENT = "content";
        String START_DATE = "start_date";
        String END_DATE = "end_date";
        String LOCATION = "location";
        String TYPE = "type";
        String LAST_EDIT_USER = "last_edit_user";
        String LAST_EDIT = "last_edit";
        String LAST_EDIT_TYPE = "last_edit_type";
        String CALENDAR_ID = "calendar_id";
        String IS_MERGED = "is_merged";
    }
}