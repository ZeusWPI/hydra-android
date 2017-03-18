package be.ugent.zeus.hydra.data.database.minerva;

import android.provider.BaseColumns;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnConstraint;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;

/**
 * Contract for the table with the courses.
 *
 * @author Niko Strijbol
 */
public final class CourseTable {

    public static final String TABLE_NAME = "minerva_courses";

    public interface Columns extends BaseColumns {
        String ID = _ID;
        String CODE = "code";
        String TITLE = "title";
        String DESCRIPTION = "description";
        String TUTOR = "tutor";
        String STUDENT = "student";
        String ACADEMIC_YEAR = "academic_year";
    }

    /**
     * @return The SQL to create this table.
     */
    public static String createTableQuery() {
        Column id = new Column(Columns.ID, ColumnType.TEXT, ColumnConstraint.PRIMARY_KEY);
        Column code = new Column(Columns.CODE, ColumnType.TEXT);
        Column title = new Column(Columns.TITLE, ColumnType.TEXT);
        Column description = new Column(Columns.DESCRIPTION, ColumnType.TEXT);
        Column tutor = new Column(Columns.TUTOR, ColumnType.TEXT);
        Column student = new Column(Columns.STUDENT, ColumnType.TEXT);
        Column year = new Column(Columns.ACADEMIC_YEAR, ColumnType.INTEGER);

        return SQLiteQueryBuilder.create().table(TABLE_NAME)
                .column(id)
                .column(code)
                .column(title)
                .column(description)
                .column(tutor)
                .column(student)
                .column(year)
                .build();
    }

    /**
     * @return The SQL to drop this table if it exists.
     */
    public static String dropIfExistQuery() {
        return SQLiteQueryBuilder.drop().table(TABLE_NAME).ifExists().build();
    }
}