package be.ugent.zeus.hydra.data.database.minerva;

import android.provider.BaseColumns;

import be.ugent.zeus.hydra.data.database.ForeignKeyColumn;
import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnConstraint;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;

/**
 * Contract for the table with the announcements.
 *
 * @author Niko Strijbol
 */
public final class AnnouncementTable {

    public static final String TABLE_NAME = "minerva_announcements";


    public interface Columns extends BaseColumns {
        //Columns for the data
        String ID = _ID;
        String COURSE = "course";
        String TITLE = "title";
        String CONTENT = "content";
        String EMAIL_SENT = "email_sent";
        String STICKY_UNTIL = "sticky_until";
        String LECTURER = "last_edit_user";
        String DATE = "date";

        //Columns for the meta data we use locally
        String READ_DATE = "read_at";
    }

    /**
     * @return The SQL to create this table.
     */
    public static String createTableQuery() {

        Column id = new Column(Columns.ID, ColumnType.INTEGER, ColumnConstraint.PRIMARY_KEY);
        Column course = new Column(Columns.COURSE, ColumnType.TEXT, ColumnConstraint.NOT_NULL);
        Column title = new Column(Columns.TITLE, ColumnType.TEXT);
        Column content = new Column(Columns.CONTENT, ColumnType.TEXT);
        Column emailSent = new Column(Columns.EMAIL_SENT, ColumnType.INTEGER);
        Column stickyUntil = new Column(Columns.STICKY_UNTIL, ColumnType.INTEGER);
        Column lecturer = new Column(Columns.LECTURER, ColumnType.TEXT);
        Column date = new Column(Columns.DATE, ColumnType.INTEGER);
        Column readDate = new Column(Columns.READ_DATE, ColumnType.INTEGER);
        //TODO: improve this api
        Column restraint = new ForeignKeyColumn(Columns.COURSE, CourseTable.TABLE_NAME, CourseTable.Columns.ID)
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