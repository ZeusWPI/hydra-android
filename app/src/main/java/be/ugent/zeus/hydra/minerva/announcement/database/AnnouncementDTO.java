package be.ugent.zeus.hydra.minerva.announcement.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;
import be.ugent.zeus.hydra.minerva.provider.CourseContract;
import java9.util.Objects;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

/**
 * Represents an announcement from Minerva as it is saved in the database.
 *
 * @author Niko Strijbol
 */
@Entity(
        tableName = AnnouncementTable.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(
                        entity = CourseDTO.class,
                        parentColumns = CourseContract.Columns.ID,
                        childColumns = AnnouncementTable.Columns.COURSE,
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public final class AnnouncementDTO {

    @PrimaryKey
    @ColumnInfo(name = AnnouncementTable.Columns.ID)
    private int id;
    @ColumnInfo(name = AnnouncementTable.Columns.TITLE)
    private String title;
    @ColumnInfo(name = AnnouncementTable.Columns.CONTENT)
    private String content;
    @ColumnInfo(name = AnnouncementTable.Columns.EMAIL_SENT)
    private boolean wasEmailSent;
    @ColumnInfo(name = AnnouncementTable.Columns.LECTURER)
    private String lecturer;
    @ColumnInfo(name = AnnouncementTable.Columns.DATE)
    private OffsetDateTime lastEditedAt;
    @ColumnInfo(name = AnnouncementTable.Columns.READ_DATE)
    private Instant readAt;
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