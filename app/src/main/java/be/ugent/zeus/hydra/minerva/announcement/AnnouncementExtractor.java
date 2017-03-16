package be.ugent.zeus.hydra.minerva.announcement;

import android.database.Cursor;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.database.Utils;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.utils.TtbUtils;

/**
 * Class to extract a {@link be.ugent.zeus.hydra.data.models.minerva.Announcement} from a {@link android.database.Cursor}.
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
        a.setEmailSent(Utils.intToBool(cursor.getInt(columnEmailSent)));
        a.setLecturer(cursor.getString(columnLecturer));
        a.setDate(TtbUtils.unserialize(cursor.getLong(columnDate)));
        a.setRead(TtbUtils.unserialize(cursor.getLong(columnReadDate)));

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
            extractor.columnIndex = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.ID);
            extractor.columnTitle = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.TITLE);
            extractor.columnContent = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.CONTENT);
            extractor.columnEmailSent = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.EMAIL_SENT);
            extractor.columnSticky = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.STICKY_UNTIL);
            extractor.columnLecturer = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.LECTURER);
            extractor.columnDate = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.DATE);
            extractor.columnReadDate = extractor.cursor.getColumnIndexOrThrow(AnnouncementTable.Columns.READ_DATE);
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
}