package be.ugent.zeus.hydra.data.database.minerva2.course;

import android.provider.BaseColumns;

/**
 * Contract for the table with the courses. This represents the current table and column names. This might change,
 * so you CANNOT use these in migrations.
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
        String ACADEMIC_YEAR = "academic_year";
        String ORDER = "ordering";
    }
}