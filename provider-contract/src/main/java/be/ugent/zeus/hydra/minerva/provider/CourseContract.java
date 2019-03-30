package be.ugent.zeus.hydra.minerva.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Lists the available columns in the provider for the courses.
 *
 * These are considered APIs and will not be broken in a backwards incompatible manner (e.g. columns may be added, but
 * not renamed or removed).
 *
 * @author Niko Strijbol
 * @version 1.1.0
 */
@SuppressWarnings("unused")
public interface CourseContract {

    /**
     * The name of the table. You probably need {@link Provider#CONTENT_URI} instead.
     */
    String TABLE_NAME = "minerva_courses";

    /**
     * The columns available in the provider.
     */
    interface Columns extends BaseColumns {

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
         * <li>{@code 1 << 0}: Announcements</li>
         * <li>{@code 1 << 1}: Calendar</li>
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

    interface Provider {
        /**
         * The authority of the content provider.
         */
        String AUTHORITY = "be.ugent.zeus.hydra.minerva.provider";
        /**
         * The base content URI, as defined by the Android documentation.
         */
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    }

    interface Permission {
        /**
         * The name of the permission you need to hold to read the courses.
         */
        String READ = "be.ugent.zeus.hydra.minerva.READ_COURSES";
    }

    interface Account {

        /**
         * The account type of the Minerva Account.
         */
        String TYPE = "be.ugent.zeus.hydra.minerva.account";

        /**
         * Contains the names of the data, accessible with {@link android.accounts.AccountManager#getUserData(android.accounts.Account, String)}.
         * The data mentioned below is the public API; all other data is considered private and comes without guarantees.
         */
        interface Data {
            /**
             * The UGent username.
             */
            String USERNAME = "user_name";
        }
    }
}