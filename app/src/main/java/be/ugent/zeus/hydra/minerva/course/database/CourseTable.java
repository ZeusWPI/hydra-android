package be.ugent.zeus.hydra.minerva.course.database;

import be.ugent.zeus.hydra.minerva.provider.CourseContract;

/**
 * Contract for the table with the courses. This represents the current table and column names. This might change,
 * so you CANNOT use these in migrations.
 *
 * @author Niko Strijbol
 */
@Deprecated
public interface CourseTable {

    String TABLE_NAME = CourseContract.TABLE_NAME;

    interface Columns extends CourseContract.Columns {
        // This interface is kept for compatibility reasons; we want to split the database from the provider.
        // At the moment these are the same (thus the inheritance), but this might change.
    }
}