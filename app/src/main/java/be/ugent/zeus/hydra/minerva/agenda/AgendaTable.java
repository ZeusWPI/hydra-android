package be.ugent.zeus.hydra.minerva.agenda;

import android.provider.BaseColumns;

import be.ugent.zeus.hydra.minerva.course.CourseTable;
import be.ugent.zeus.hydra.minerva.database.query.ForeignKeyColumn;
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

    //Columns for the data
    public static final String COLUMN_ID = _ID;
    public static final String COLUMN_COURSE = "course";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_LAST_EDIT_USER = "last_edit_user";
    public static final String COLUMN_LAST_EDIT = "last_edit";
    public static final String COLUMN_LAST_EDIT_TYPE = "last_edit_type";

    /**
     * @return The SQL to create this table.
     */
    public static String createTableQuery() {

        Column id = new Column(COLUMN_ID, ColumnType.INTEGER, ColumnConstraint.PRIMARY_KEY);
        Column course = new Column(COLUMN_COURSE, ColumnType.TEXT, ColumnConstraint.NOT_NULL);
        Column title = new Column(COLUMN_TITLE, ColumnType.TEXT);
        Column content = new Column(COLUMN_CONTENT, ColumnType.TEXT);
        Column startDate = new Column(COLUMN_START_DATE, ColumnType.INTEGER);
        Column endDate = new Column(COLUMN_END_DATE, ColumnType.INTEGER);
        Column location = new Column(COLUMN_LOCATION, ColumnType.TEXT);
        Column type = new Column(COLUMN_TYPE, ColumnType.TEXT);
        Column lastEditUser = new Column(COLUMN_LAST_EDIT_USER, ColumnType.TEXT);
        Column lastEdit = new Column(COLUMN_LAST_EDIT, ColumnType.INTEGER);
        Column lastEditType = new Column(COLUMN_LAST_EDIT_TYPE, ColumnType.TEXT);
        //TODO: improve this api

        Column restraint = new ForeignKeyColumn(COLUMN_COURSE, CourseTable.TABLE_NAME, CourseTable.COLUMN_ID)
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