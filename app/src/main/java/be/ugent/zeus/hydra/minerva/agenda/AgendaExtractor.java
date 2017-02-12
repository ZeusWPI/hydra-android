package be.ugent.zeus.hydra.minerva.agenda;

import android.database.Cursor;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.utils.TtbUtils;

/**
 * Class to extract a {@link AgendaItem} from a {@link Cursor}.
 *
 * This class should be set up once per database request. Build this class with the builder class.
 *
 * The builder class needs to be provided with the Cursor. Note that this class does NOT advance the cursor.
 *
 * @author Niko Strijbol
 */
class AgendaExtractor {

    private int columnIndex;
    private int columnTitle;
    private int columnContent;
    private int columnStartDate;
    private int columnEndDate;
    private int columnLocation;
    private int columnType;
    private int columnLastEditUser;
    private int columnLastEdit;
    private int columnLastEditType;
    private int columnCalendarId;

    private final Cursor cursor;

    private AgendaExtractor(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Get an announcement from the cursor. To re-iterate, this does NOT affect the position of the cursor in any way.
     *
     * @return The announcement.
     */
    public AgendaItem getAgendaItem(Course course) {
        AgendaItem a = new AgendaItem();
        a.setCourse(course);
        a.setItemId(cursor.getInt(columnIndex));
        a.setTitle(cursor.getString(columnTitle));
        a.setContent(cursor.getString(columnContent));
        a.setStartDate(TtbUtils.unserialize(cursor.getLong(columnStartDate)));
        a.setEndDate(TtbUtils.unserialize(cursor.getLong(columnEndDate)));
        a.setLocation(cursor.getString(columnLocation));
        a.setType(cursor.getString(columnType));
        a.setLastEditUser(cursor.getString(columnLastEditUser));
        a.setLastEdited(TtbUtils.unserialize(cursor.getLong(columnLastEdit)));
        a.setLastEditType(cursor.getString(columnLastEditType));
        a.setCalendarId(cursor.getLong(columnCalendarId));

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

    public int getColumnStartDate() {
        return columnStartDate;
    }

    public int getColumnEndDate() {
        return columnEndDate;
    }

    public int getColumnLocation() {
        return columnLocation;
    }

    public int getColumnType() {
        return columnType;
    }

    public int getColumnLastEditUser() {
        return columnLastEditUser;
    }

    public int getColumnLastEdit() {
        return columnLastEdit;
    }

    public int getColumnLastEditType() {
        return columnLastEditType;
    }

    /**
     * Builder class.
     */
    @SuppressWarnings("unused")
    public static class Builder {

        private final AgendaExtractor extractor;

        public Builder(@NonNull Cursor cursor) {
            this.extractor = new AgendaExtractor(cursor);
        }

        /**
         * Set every column int to the default value. The default value is the column name in the database table.
         */
        public Builder defaults() {
            extractor.columnIndex = extract(AgendaTable.Columns.ID);
            extractor.columnTitle = extract(AgendaTable.Columns.TITLE);
            extractor.columnContent = extract(AgendaTable.Columns.CONTENT);
            extractor.columnStartDate = extract(AgendaTable.Columns.START_DATE);
            extractor.columnEndDate = extract(AgendaTable.Columns.END_DATE);
            extractor.columnLocation = extract(AgendaTable.Columns.LOCATION);
            extractor.columnType = extract(AgendaTable.Columns.TYPE);
            extractor.columnLastEditUser = extract(AgendaTable.Columns.LAST_EDIT_USER);
            extractor.columnLastEdit = extract(AgendaTable.Columns.LAST_EDIT);
            extractor.columnLastEditType = extract(AgendaTable.Columns.LAST_EDIT_TYPE);
            extractor.columnCalendarId = extract(AgendaTable.Columns.CALENDAR_ID);
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

        public Builder columnStartDate(String columnName) {
            extractor.columnStartDate = extract(columnName);
            return this;
        }

        public Builder columnEndDate(String columnName) {
            extractor.columnEndDate = extract(columnName);
            return this;
        }

        public Builder columnLocation(String columnName) {
            extractor.columnLocation = extract(columnName);
            return this;
        }

        public Builder columnType(String columnName) {
            extractor.columnType = extract(columnName);
            return this;
        }

        public Builder columnLastEditUser(String columnName) {
            extractor.columnLastEditUser = extract(columnName);
            return this;
        }

        public Builder columnLastEdit(String columnName) {
            extractor.columnLastEdit = extract(columnName);
            return this;
        }

        public Builder columnLastEditType(String columnName) {
            extractor.columnLastEditType = extract(columnName);
            return this;
        }

        public Builder columnCalendarId(String columnName) {
            extractor.columnCalendarId = extract(columnName);
            return this;
        }

        public AgendaExtractor build() {
            return extractor;
        }
    }
}