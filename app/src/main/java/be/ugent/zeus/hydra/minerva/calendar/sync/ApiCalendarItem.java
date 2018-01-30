package be.ugent.zeus.hydra.minerva.calendar.sync;


import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.OffsetDateTime;

/**
 * Represents the database model of a calendar item.
 *
 * This class is visible because of Room constraints.
 *
 * @author Niko Strijbol
 */
final class ApiCalendarItem {
    @SerializedName("item_id")
    public int id;
    public String title;
    public String content;
    @SerializedName("start_date")
    @JsonAdapter(DateTypeConverters.GsonOffset.class)
    public OffsetDateTime startDate;
    @SerializedName("end_date")
    @JsonAdapter(DateTypeConverters.GsonOffset.class)
    public OffsetDateTime endDate;
    public String location;
    public String type;
    @SerializedName("last_edit_user")
    public String lastEditedUser;
    @SerializedName("last_edit_time")
    @JsonAdapter(DateTypeConverters.GsonOffset.class)
    public OffsetDateTime lastEditDate;
    @SerializedName("last_edit_type")
    public String lastEditType;
    @SerializedName("course_id")
    public String courseId;
}