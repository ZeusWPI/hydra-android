package be.ugent.zeus.hydra.minerva.announcement;

import android.database.Cursor;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.Date;

/**
 * Class to extract a {@link be.ugent.zeus.hydra.models.minerva.Announcement} from a {@link android.database.Cursor}.
 *
 * This class should be set up once per database request. Build this class with the builder class.
 *
 * The builder class needs to be provided with the Cursor. Note that this class does NOT advance the cursor.
 *
 * @author Niko Strijbol
 */
class AnnouncementExtractor {

    private int columnIndex;
    private int columnTitle;
    private int columnContent;
    private int columnEmailSent;
    private int columnSticky;
    private int columnLecturer;
    private int columnDate;
    private int columnReadDate;

    private final Cursor cursor;

    private AnnouncementExtractor(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Get an announcement from the cursor. To re-iterate, this does NOT affect the position of the cursor in any way.
     *
     * @return The announcement.
     */
    public Announcement getAnnouncement(Course course) {
        Announcement a = new Announcement();
        a.setCourse(course);
        a.setItemId(cursor.getInt(columnIndex));
        a.setTitle(cursor.getString(columnTitle));
        a.setContent(cursor.getString(columnContent));
        a.setEmailSent(intToBool(cursor.getInt(columnEmailSent)));
        a.setLecturer(cursor.getString(columnLecturer));
        a.setDate(longToDate(cursor.getLong(columnDate)));
        a.setRead(longToDate(cursor.getLong(columnReadDate)));

        return a;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getColumnTitle() {
        return columnTitle;
    }

    public int getColumnContent() {
        return columnContent;
    }

    public int getColumnEmailSent() {
        return columnEmailSent;
    }

    public int getColumnSticky() {
        return columnSticky;
    }

    public int getColumnLecturer() {
        return columnLecturer;
    }

    public int getColumnDate() {
        return columnDate;
    }

    public int getColumnReadDate() {
        return columnReadDate;
    }

    /**
     * Builder class.
     */
    @SuppressWarnings("unused")
    public static class Builder {

        private final AnnouncementExtractor extractor;

        public Builder(@NonNull Cursor cursor) {
            this.extractor = new AnnouncementExtractor(cursor);
        }

        /**
         * Set every column int to the default value. The default value is the column name in the database table.
         */
        public Builder defaults() {
            extractor.columnIndex = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_ID);
            extractor.columnTitle = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_TITLE);
            extractor.columnContent = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_CONTENT);
            extractor.columnEmailSent = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_EMAIL_SENT);
            extractor.columnSticky = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_STICKY_UNTIL);
            extractor.columnLecturer = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_LECTURER);
            extractor.columnDate = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_DATE);
            extractor.columnReadDate = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.COLUMN_READ_DATE);
            return this;
        }

        private int extract(String column) {
            return extractor.cursor.getColumnIndexOrThrow(column);
        }

        public Builder columnIndex(String columnIndex) {
            extractor.columnIndex = extract(columnIndex);
            return this;
        }

        public Builder columnTitle(String columnTitle) {
            extractor.columnTitle = extract(columnTitle);
            return this;
        }

        public Builder columnContent(String columnContent) {
            extractor.columnContent = extract(columnContent);
            return this;
        }

        public Builder columnEmailSent(String columnEmailSent) {
            extractor.columnEmailSent = extract(columnEmailSent);
            return this;
        }

        public Builder columnSticky(String columnSticky) {
            extractor.columnSticky = extract(columnSticky);
            return this;
        }

        public Builder columnLecturer(String columnLecturer) {
            extractor.columnLecturer = extract(columnLecturer);
            return this;
        }

        public Builder columnDate(String columnDate) {
            extractor.columnDate = extract(columnDate);
            return this;
        }

        public Builder columnReadDate(String columnReadDate) {
            extractor.columnReadDate = extract(columnReadDate);
            return this;
        }

        public AnnouncementExtractor build() {
            return extractor;
        }
    }

    /**
     * @return True if {@code integer} is 1, else false.
     */
    public static boolean intToBool(int integer) {
        return integer == 1;
    }

    /**
     * @return 1 if {@code bool} is true, else 0.
     */
    public static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    /**
     * @return Null if {@code date} is 0, else the date represented by this long.
     */
    public static Date longToDate(long date) {
        if(date == 0) {
            return null;
        } else {
            return new Date(date);
        }
    }
}