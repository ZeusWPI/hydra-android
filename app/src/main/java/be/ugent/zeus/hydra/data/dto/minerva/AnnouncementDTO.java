package be.ugent.zeus.hydra.data.dto.minerva;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import be.ugent.zeus.hydra.data.database.minerva.AnnouncementTable;
import be.ugent.zeus.hydra.data.database.minerva.CourseTable;
import be.ugent.zeus.hydra.data.dto.DateTypeConverters;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

/**
 * Represents an announcement from Minerva.
 *
 * @author Niko Strijbol
 */
@Entity(
        tableName = AnnouncementTable.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(
                        entity = CourseDTO.class,
                        parentColumns = CourseTable.Columns.ID,
                        childColumns = AnnouncementTable.Columns.COURSE,
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public final class AnnouncementDTO {

    @ColumnInfo(name = AnnouncementTable.Columns.TITLE)
    private String title;
    @ColumnInfo(name = AnnouncementTable.Columns.CONTENT)
    private String content;
    @SerializedName("email_sent")
    @ColumnInfo(name = AnnouncementTable.Columns.EMAIL_SENT)
    private boolean wasEmailSent;
    @SerializedName("item_id")
    @PrimaryKey
    @ColumnInfo(name = AnnouncementTable.Columns.ID)
    private int id;
    @SerializedName("last_edit_user")
    @ColumnInfo(name = AnnouncementTable.Columns.LECTURER)
    private String lecturer;
    @SerializedName("last_edit_time")
    @JsonAdapter(DateTypeConverters.GsonOffset.class)
    @ColumnInfo(name = AnnouncementTable.Columns.DATE)
    private OffsetDateTime lastEditedAt;
    @SerializedName("read_at") // For testing
    @ColumnInfo(name = AnnouncementTable.Columns.READ_DATE)
    private Instant readAt;
    @SerializedName("course_id")
    @ColumnInfo(name = AnnouncementTable.Columns.COURSE, index = true)
    private String courseId;

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

    public boolean isWasEmailSent() {
        return wasEmailSent;
    }

    public void setWasEmailSent(boolean wasEmailSent) {
        this.wasEmailSent = wasEmailSent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OffsetDateTime getLastEditedAt() {
        return lastEditedAt;
    }

    public void setLastEditedAt(OffsetDateTime lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnouncementDTO that = (AnnouncementDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}