package be.ugent.zeus.hydra.data.models.minerva;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.TtbUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * @author Niko Strijbol
 */
public final class AgendaItem implements Serializable, Parcelable {

    public static final long NO_CALENDAR_ID = -1;

    public static final String AGENDA_URI = "hydra://minerva/calendar/";

    @SerializedName("item_id")
    private int itemId;
    private String title;
    private String content;
    @SerializedName("start_date")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime startDate;
    @SerializedName("end_date")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime endDate;
    private String location;
    private String type;
    @SerializedName("last_edit_user")
    private String lastEditUser;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    @SerializedName("last_edit_time")
    private ZonedDateTime lastEdited;
    @SerializedName("last_edit_type")
    private String lastEditType;
    private Course course;
    private long calendarId = NO_CALENDAR_ID;

    //This is used sometimes as well, when we don't need the full course.
    @SerializedName("course_id")
    private String courseId;

    // This indicates this is actually a merged event, consisting of multiple events from Minerva.
    // If this is the case, the ID is from one of the events.
    private boolean isMerged = false;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Course getCourse() {
        return course;
    }

    public String getCourseId() {
        if (courseId == null && course == null) {
            return null;
        } else {
            if (courseId == null) {
                return course.getId();
            } else {
                return courseId;
            }
        }
    }

    public void setCourse(Course course) {
        this.course = course;
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

    public LocalDate getLocalStartDate() {
        return DateUtils.toLocalDateTime(getStartDate()).toLocalDate();
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

    public String getLastEditUser() {
        return lastEditUser;
    }

    public void setLastEditUser(String lastEditUser) {
        this.lastEditUser = lastEditUser;
    }

    public ZonedDateTime getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(ZonedDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getLastEditType() {
        return lastEditType;
    }

    public void setLastEditType(String lastEditType) {
        this.lastEditType = lastEditType;
    }

    public boolean isMerged() {
        return isMerged;
    }

    public void setMerged(boolean merged) {
        isMerged = merged;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUri() {
        return AGENDA_URI + getItemId();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.itemId);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeSerializable(this.startDate);
        dest.writeSerializable(this.endDate);
        dest.writeString(this.location);
        dest.writeString(this.type);
        dest.writeString(this.lastEditUser);
        dest.writeLong(TtbUtils.serialize(this.lastEdited));
        dest.writeString(this.lastEditType);
        dest.writeParcelable(this.course, flags);
        dest.writeString(this.courseId);
        dest.writeByte((byte) (isMerged ? 1 : 0));
    }

    public AgendaItem() {
    }

    private AgendaItem(Parcel in) {
        this.itemId = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.startDate = (ZonedDateTime) in.readSerializable();
        this.endDate = (ZonedDateTime) in.readSerializable();
        this.location = in.readString();
        this.type = in.readString();
        this.lastEditUser = in.readString();
        this.lastEdited = TtbUtils.unserialize(in.readLong());
        this.lastEditType = in.readString();
        this.course = in.readParcelable(Course.class.getClassLoader());
        this.courseId = in.readString();
        this.isMerged = in.readByte() == 1;
    }

    public static final Parcelable.Creator<AgendaItem> CREATOR = new Parcelable.Creator<AgendaItem>() {
        @Override
        public AgendaItem createFromParcel(Parcel source) {
            return new AgendaItem(source);
        }

        @Override
        public AgendaItem[] newArray(int size) {
            return new AgendaItem[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaItem that = (AgendaItem) o;
        return itemId == that.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    public long getCalendarId() {
        return calendarId;
    }

    public AgendaItem setCalendarId(long calendarId) {
        this.calendarId = calendarId;
        return this;
    }
}
