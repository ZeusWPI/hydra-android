package be.ugent.zeus.hydra.minerva.provider.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Lists the available columns in the provider for the courses.
 *
 * These are considered APIs and will not be broken in a backwards incompatible manner (e.g. columns may be added, but
 * not renamed or removed).
 *
 * @version 1.0.0
 *
 * @author Niko Strijbol
 */
public final class CourseContract {

    /**
     * The name of the table. You probably need {@link Provider#CONTENT_URI} instead.
     */
    public static final String TABLE_NAME = "minerva_courses";

    /**
     * The columns available in the provider.
     */
    public interface Columns extends BaseColumns {

        /**
         * The unique ID of a course.
         * <p>Type: TEXT</p>
         */
        String ID = _ID;
        /**
         * The course's code.
         * <p>Type: TEXT</p>
         */
        String CODE = "code";
        /**
         * The name of the course.
         * <p>Type: TEXT</p>
         */
        String TITLE = "title";
        /**
         * An optional description of the course.
         * <p>Type: NULLABLE TEXT</p>
         */
        String DESCRIPTION = "description";
        /**
         * The tutor of the course.
         * <p>Type: TEXT</p>
         */
        String TUTOR = "tutor";
        /**
         * The first year of the academic year in which this course is organised. Will be 0 if there is no year.
         * <p>Type: INTEGER</p>
         */
        String ACADEMIC_YEAR = "academic_year";
        /**
         * The user-defined ordering of the course. The usability of this column is not guaranteed.
         * <p>Type: INTEGER</p>
         */
        String ORDER = "ordering";
        /**
         * Int flag depicting which modules are disabled. The possible values at this time are:
         * <ul>
         *     <li>{@code 1 << 0}: Announcements</li>
         *     <li>{@code 1 << 1}: Calendar</li>
         * </ul>
         * <p>Type: INTEGER</p>
         */
        String DISABLED_MODULES = "disabled_modules";
        /**
         * Boolean representing if the announcements should be ignored for this course or not.
         * <p>Type: INTEGER</p>
         */
        String IGNORE_ANNOUNCEMENTS = "ignore_announcements";
        /**
         * Boolean representing if the calendar should be ignored for this course or not.
         * <p>Type: INTEGER</p>
         */
        String IGNORE_CALENDAR = "ignore_calendar";
    }

    public interface Provider {
        /**
         * The authority of the content provider.
         */
        String AUTHORITY = "be.ugent.zeus.hydra.minerva.provider";
        /**
         * The base content URI, as defined by the Android documentation.
         */
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    }
}