package be.ugent.zeus.hydra.data.dto.minerva;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import be.ugent.zeus.hydra.data.database.minerva2.agenda.AgendaTable;
import be.ugent.zeus.hydra.data.database.minerva2.course.CourseTable;
import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;
import org.threeten.bp.ZonedDateTime;

import static be.ugent.zeus.hydra.domain.models.minerva.AgendaItem.NO_CALENDAR_ID;

/**
 * Represents an agenda item as DTO.
 *
 * @author Niko Strijbol
 */
@Entity(
        tableName = AgendaTable.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(
                        entity = CourseDTO.class,
                        parentColumns = CourseTable.Columns.ID,
                        childColumns = AgendaTable.Columns.COURSE
                )
        }
)
public class AgendaItemDTO {

    @SerializedName("item_id")
    @PrimaryKey
    @ColumnInfo(name = AgendaTable.Columns.ID)
    private int id;
    @ColumnInfo(name = AgendaTable.Columns.TITLE)
    private String title;
    @ColumnInfo(name = AgendaTable.Columns.CONTENT)
    private String content;
    @SerializedName("start_date")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    @ColumnInfo(name = AgendaTable.Columns.START_DATE)
    private ZonedDateTime startDate;
    @SerializedName("end_date")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    @ColumnInfo(name = AgendaTable.Columns.END_DATE)
    private ZonedDateTime endDate;
    @ColumnInfo(name = AgendaTable.Columns.LOCATION)
    private String location;
    @ColumnInfo(name = AgendaTable.Columns.TYPE)
    private String type;
    @SerializedName("last_edit_user")
    @ColumnInfo(name = AgendaTable.Columns.LAST_EDIT_USER)
    private String lastEditedUser;
    @SerializedName("last_edit_time")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    @ColumnInfo(name = AgendaTable.Columns.LAST_EDIT)
    private ZonedDateTime lastEditDate;
    @SerializedName("last_edit_type")
    @ColumnInfo(name = AgendaTable.Columns.LAST_EDIT_TYPE)
    private String lastEditType;
    @SerializedName("course_id")
    @ColumnInfo(name = AgendaTable.Columns.COURSE, index = true)
    private String courseId;
    @SerializedName("calendar_id") // For testing
    @ColumnInfo(name = AgendaTable.Columns.CALENDAR_ID)
    private long calendarId = NO_CALENDAR_ID;
    @SerializedName("is_merged") // For testing
    @ColumnInfo(name = AgendaTable.Columns.IS_MERGED)
    private boolean isMerged = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastEditedUser() {
        return lastEditedUser;
    }

    public void setLastEditedUser(String lastEditedUser) {
        this.lastEditedUser = lastEditedUser;
    }

    public ZonedDateTime getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(ZonedDateTime lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public String getLastEditType() {
        return lastEditType;
    }

    public void setLastEditType(String lastEditType) {
        this.lastEditType = lastEditType;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public boolean isMerged() {
        return isMerged;
    }

    public void setMerged(boolean merged) {
        isMerged = merged;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaItemDTO that = (AgendaItemDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}