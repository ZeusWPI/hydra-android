package be.ugent.zeus.hydra.minerva.calendar.sync;


import com.squareup.moshi.Json;
import org.threeten.bp.OffsetDateTime;

/**
 * Represents the database model of a calendar item.
 *
 * This class is visible because of Room constraints.
 *
 * @author Niko Strijbol
 */
final class ApiCalendarItem {
    @Json(name = "item_id")
    public int id;
    public String title;
    public String content;
    @Json(name = "start_date")
    public OffsetDateTime startDate;
    @Json(name = "end_date")
    public OffsetDateTime endDate;
    public String location;
    public String type;
    @Json(name = "last_edit_user")
    public String lastEditedUser;
    @Json(name = "last_edit_time")
    public OffsetDateTime lastEditDate;
    @Json(name = "last_edit_type")
    public String lastEditType;
    @Json(name = "course_id")
    public String courseId;
}