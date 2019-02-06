package be.ugent.zeus.hydra.minerva.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.utils.DateUtils;
import java9.util.Objects;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;

/**
 * @author Niko Strijbol
 */
public final class AgendaItem implements Parcelable {

    public static final long NO_CALENDAR_ID = -1;

    public static final String AGENDA_URI = "hydra://minerva/calendar/";

    private int itemId;
    private String title;
    private String content;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String location;
    private String type;
    private String lastEditedUser;
    private OffsetDateTime lastEdited;
    private String lastEditType;
    private Course course;
    private long calendarId = NO_CALENDAR_ID;

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

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public LocalDate getLocalStartDate() {
        return DateUtils.toLocalDateTime(getStartDate()).toLocalDate();
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
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

    public OffsetDateTime getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(OffsetDateTime lastEdited) {
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
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.startDate));
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.endDate));
        dest.writeString(this.location);
        dest.writeString(this.type);
        dest.writeString(this.lastEditedUser);
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.lastEdited));
        dest.writeString(this.lastEditType);
        dest.writeParcelable(this.course, flags);
        dest.writeByte((byte) (isMerged ? 1 : 0));
    }

    public AgendaItem() {
    }

    private AgendaItem(Parcel in) {
        this.itemId = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.startDate = DateTypeConverters.toOffsetDateTime(in.readString());
        this.endDate = DateTypeConverters.toOffsetDateTime(in.readString());
        this.location = in.readString();
        this.type = in.readString();
        this.lastEditedUser = in.readString();
        this.lastEdited = DateTypeConverters.toOffsetDateTime(in.readString());
        this.lastEditType = in.readString();
        this.course = in.readParcelable(Course.class.getClassLoader());
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
