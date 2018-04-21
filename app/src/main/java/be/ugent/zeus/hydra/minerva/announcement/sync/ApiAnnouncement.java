package be.ugent.zeus.hydra.minerva.announcement.sync;

import com.squareup.moshi.Json;
import org.threeten.bp.OffsetDateTime;

/**
 * Api announcement.
 *
 * @author Niko Strijbol
 */
final class ApiAnnouncement {
    public String title;
    public String content;
    @Json(name = "email_sent")
    public boolean wasEmailSent;
    @Json(name = "item_id")
    public int id;
    @Json(name = "last_edit_user")
    public String lecturer;
    @Json(name = "last_edit_time")
    public OffsetDateTime lastEditedAt;
    @Json(name = "course_id")
    public String courseId;
}