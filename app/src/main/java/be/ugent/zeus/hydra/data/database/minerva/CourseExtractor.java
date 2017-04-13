package be.ugent.zeus.hydra.data.database.minerva;

import android.database.Cursor;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.minerva.Course;

/**
 * Class to extract a {@link be.ugent.zeus.hydra.data.models.minerva.Course} from a {@link android.database.Cursor}.
 *
 * This class should be set up once per database request. Build this class with the builder class.
 *
 * The builder class needs to be provided with the Cursor. Note that this class does NOT advance the cursor.
 *
 * @author Niko Strijbol
 */
class CourseExtractor {

    //Column ints
    private int columnId;
    private int columnCode;
    private int columnTitle;
    private int columnDesc;
    private int columnTutor;
    private int columnStudent;
    private int columnYear;

    private final Cursor cursor;

    private CourseExtractor(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Get a course from the cursor. To re-iterate, this does NOT affect the position of the cursor in any way.
     *
     * @return The course.
     */
    public Course getCourse() {
        Course course = new Course();
        course.setId(cursor.getString(columnId));
        course.setCode(cursor.getString(columnCode));
        course.setTitle(cursor.getString(columnTitle));
        course.setDescription(cursor.getString(columnDesc));
        course.setTutorName(cursor.getString(columnTutor));
        course.setStudent(cursor.getString(columnStudent));
        course.setAcademicYear(cursor.getInt(columnYear));

        return course;
    }

    public int getColumnId() {
        return columnId;
    }

    public int getColumnCode() {
        return columnCode;
    }

    public int getColumnTitle() {
        return columnTitle;
    }

    public int getColumnDesc() {
        return columnDesc;
    }

    public int getColumnTutor() {
        return columnTutor;
    }

    public int getColumnStudent() {
        return columnStudent;
    }

    public int getColumnYear() {
        return columnYear;
    }

    /**
     * Builder class.
     */
    @SuppressWarnings("unused")
    public static class Builder {

        private final CourseExtractor extractor;

        /**
         * @param cursor The cursor to be used.
         */
        public Builder(@NonNull Cursor cursor) {
            this.extractor = new CourseExtractor(cursor);
        }

        /**
         * Set every column int to the default value. The default value is the column name in the database table.
         */
        public Builder defaults() {

            extractor.columnId = extractor.cursor.getColumnIndexOrThrow(CourseTable.Columns.ID);
            extractor.columnCode = extractor.cursor.getColumnIndexOrThrow(CourseTable.Columns.CODE);
            extractor.columnTitle = extractor.cursor.getColumnIndexOrThrow(CourseTable.Columns.TITLE);
            extractor.columnDesc = extractor.cursor.getColumnIndexOrThrow(CourseTable.Columns.DESCRIPTION);
            extractor.columnTutor = extractor.cursor.getColumnIndexOrThrow(CourseTable.Columns.TUTOR);
            extractor.columnStudent = extractor.cursor.getColumnIndexOrThrow(CourseTable.Columns.STUDENT);
            extractor.columnYear = extractor.cursor.getColumnIndexOrThrow(CourseTable.Columns.ACADEMIC_YEAR);

            return this;
        }

        private int extract(String column) {
            return extractor.cursor.getColumnIndexOrThrow(column);
        }

        public Builder columnId(String columnId) {
            extractor.columnId = extract(columnId);
            return this;
        }

        public Builder columnCode(String columnCode) {
            extractor.columnCode = extract(columnCode);
            return this;
        }

        public Builder columnTitle(String columnTitle) {
            extractor.columnTitle = extract(columnTitle);
            return this;
        }

        public Builder columnDesc(String columnDesc) {
            extractor.columnDesc = extract(columnDesc);
            return this;
        }

        public Builder columnTutor(String columnTutor) {
            extractor.columnTutor = extract(columnTutor);
            return this;
        }

        public Builder columnStudent(String columnStudent) {
            extractor.columnStudent = extract(columnStudent);
            return this;
        }

        public Builder columnYear(String columnYear) {
            extractor.columnYear = extract(columnYear);
            return this;
        }

        /**
         * @return The built instance to use.
         */
        public CourseExtractor build() {
            return extractor;
        }
    }
}