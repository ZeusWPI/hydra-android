package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;
import java8.util.Objects;
import org.threeten.bp.OffsetDateTime;

/**
 * Api announcement.
 *
 * @author Niko Strijbol
 */
final class ApiAnnouncement {
    public String title;
    public String content;
    @SerializedName("email_sent")
    @Json(name = "email_sent")
    public boolean wasEmailSent;
    @Json(name = "item_id")
    @SerializedName("item_id")
    public int id;
    @SerializedName("last_edit_user")
    @Json(name = "last_edit_user")
    public String lecturer;
    @SerializedName("last_edit_time")
    @Json(name = "last_edit_time")
    @JsonAdapter(DateTypeConverters.GsonOffset.class)
    public OffsetDateTime lastEditedAt;
    @SerializedName("course_id")
    @Json(name = "course_id")
    public String courseId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiAnnouncement that = (ApiAnnouncement) o;
        return wasEmailSent == that.wasEmailSent &&
                id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(lecturer, that.lecturer) &&
                Objects.equals(lastEditedAt, that.lastEditedAt) &&
                Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, wasEmailSent, id, lecturer, lastEditedAt, courseId);
    }
}