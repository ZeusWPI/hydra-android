package be.ugent.zeus.hydra.minerva.announcement;

import android.provider.BaseColumns;

import be.ugent.zeus.hydra.minerva.course.CourseTable;
import be.ugent.zeus.hydra.minerva.database.query.ForeignKeyColumn;
import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnConstraint;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;

/**
 * Contract for the table with the announcements.
 *
 * @author Niko Strijbol
 */
public final class AnnouncementTable implements BaseColumns {

    public static final String TABLE_NAME = "minerva_announcements";

    //Columns for the data
    public static final String COLUMN_ID = _ID;
    public static final String COLUMN_COURSE = "course";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_EMAIL_SENT = "email_sent";
    public static final String COLUMN_STICKY_UNTIL = "sticky_until";
    public static final String COLUMN_LECTURER = "last_edit_user";
    public static final String COLUMN_DATE = "date";


    //Columns for the meta data we use locally
    public static final String COLUMN_READ_DATE = "read_at";

    /**
     * @return The SQL to create this table.
     */
    public static String createTableQuery() {

        Column id = new Column(COLUMN_ID, ColumnType.INTEGER, ColumnConstraint.PRIMARY_KEY);
        Column course = new Column(COLUMN_COURSE, ColumnType.TEXT, ColumnConstraint.NOT_NULL);
        Column title = new Column(COLUMN_TITLE, ColumnType.TEXT);
        Column content = new Column(COLUMN_CONTENT, ColumnType.TEXT);
        Column emailSent = new Column(COLUMN_EMAIL_SENT, ColumnType.INTEGER);
        Column stickyUntil = new Column(COLUMN_STICKY_UNTIL, ColumnType.INTEGER);
        Column lecturer = new Column(COLUMN_LECTURER, ColumnType.TEXT);
        Column date = new Column(COLUMN_DATE, ColumnType.INTEGER);
        Column readDate = new Column(COLUMN_READ_DATE, ColumnType.INTEGER);
        //TODO: improve this api
        Column restraint = new ForeignKeyColumn(COLUMN_COURSE, CourseTable.TABLE_NAME, CourseTable.COLUMN_ID)
                .cascade(true);

        return SQLiteQueryBuilder.create().table(TABLE_NAME)
                .column(id)
                .column(course)
                .column(title)
                .column(content)
                .column(emailSent)
                .column(stickyUntil)
                .column(lecturer)
                .column(date)
                .column(readDate)
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