package be.ugent.zeus.hydra.data.database.minerva2.announcement;

import android.provider.BaseColumns;

/**
 * Contract for the table with the announcements. This represents the current table and column names. This might change,
 * so you CANNOT use these in migrations.
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
        String LECTURER = "last_edit_user";
        String DATE = "date";

        //Columns for the meta data we use locally
        String READ_DATE = "read_at";
    }
}