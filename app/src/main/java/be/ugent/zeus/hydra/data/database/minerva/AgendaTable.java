package be.ugent.zeus.hydra.data.database.minerva;

import android.provider.BaseColumns;

import be.ugent.zeus.hydra.data.database.ForeignKeyColumn;
import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnConstraint;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;

/**
 * Contract for the table with the agenda items.
 *
 * @author Niko Strijbol
 */
public final class AgendaTable implements BaseColumns {

    public static final String TABLE_NAME = "minerva_agenda";

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
    }

    /**
     * @return The SQL to create this table.
     */
    public static String createTableQuery() {

        Column id = new Column(Columns.ID, ColumnType.INTEGER, ColumnConstraint.PRIMARY_KEY);
        Column course = new Column(Columns.COURSE, ColumnType.TEXT, ColumnConstraint.NOT_NULL);
        Column title = new Column(Columns.TITLE, ColumnType.TEXT);
        Column content = new Column(Columns.CONTENT, ColumnType.TEXT);
        Column startDate = new Column(Columns.START_DATE, ColumnType.INTEGER);
        Column endDate = new Column(Columns.END_DATE, ColumnType.INTEGER);
        Column location = new Column(Columns.LOCATION, ColumnType.TEXT);
        Column type = new Column(Columns.TYPE, ColumnType.TEXT);
        Column lastEditUser = new Column(Columns.LAST_EDIT_USER, ColumnType.TEXT);
        Column lastEdit = new Column(Columns.LAST_EDIT, ColumnType.INTEGER);
        Column lastEditType = new Column(Columns.LAST_EDIT_TYPE, ColumnType.TEXT);
        Column calenderId = new Column(Columns.CALENDAR_ID, ColumnType.INTEGER);

        Column restraint = new ForeignKeyColumn(Columns.COURSE, CourseTable.TABLE_NAME, CourseTable.Columns.ID)
                .cascade(true);

        return SQLiteQueryBuilder.create().table(TABLE_NAME)
                .column(id)
                .column(course)
                .column(title)
                .column(content)
                .column(startDate)
                .column(endDate)
                .column(location)
                .column(type)
                .column(lastEditUser)
                .column(lastEdit)
                .column(lastEditType)
                .column(calenderId)
                .column(restraint)
                .build();
    }

    /**
     * @return The SQL to drop this table if it exists.
     */
    public static String dropIfExistQuery() {
        return SQLiteQueryBuilder.drop().table(TABLE_NAME).ifExists().build();
    }
}