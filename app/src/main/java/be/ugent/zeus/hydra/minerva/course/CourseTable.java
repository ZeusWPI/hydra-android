package be.ugent.zeus.hydra.minerva.course;

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
public final class CourseTable implements BaseColumns {

    public static final String TABLE_NAME = "minerva_courses";

    //Columns
    public static final String COLUMN_ID = _ID;
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TUTOR = "tutor";
    public static final String COLUMN_STUDENT = "student";
    public static final String COLUMN_ACADEMIC_YEAR = "academic_year";

    /**
     * @return The SQL to create this table.
     */
    public static String createTableQuery() {
        Column id = new Column(COLUMN_ID, ColumnType.TEXT, ColumnConstraint.PRIMARY_KEY);
        Column code = new Column(COLUMN_CODE, ColumnType.TEXT);
        Column title = new Column(COLUMN_TITLE, ColumnType.TEXT);
        Column description = new Column(COLUMN_DESCRIPTION, ColumnType.TEXT);
        Column tutor = new Column(COLUMN_TUTOR, ColumnType.TEXT);
        Column student = new Column(COLUMN_STUDENT, ColumnType.TEXT);
        Column year = new Column(COLUMN_ACADEMIC_YEAR, ColumnType.INTEGER);

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