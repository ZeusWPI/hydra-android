package be.ugent.zeus.hydra.models.minerva;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.converters.ISO8601DateJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Niko Strijbol
 */
public class AgendaItem implements Serializable, Parcelable {

    @SerializedName("item_id")
    private int itemId;
    private String title;
    private String content;
    @SerializedName("start_date")
    @JsonAdapter(ISO8601DateJsonAdapter.class)
    private Date startDate;
    @SerializedName("end_date")
    @JsonAdapter(ISO8601DateJsonAdapter.class)
    private Date endDate;
    private String location;
    private String type;
    @SerializedName("last_edit_user")
    private String lastEditUser;
    @JsonAdapter(ISO8601DateJsonAdapter.class)
    @SerializedName("last_edit_time")
    private Date lastEdited;
    @SerializedName("last_edit_type")
    private String lastEditType;
    private Course course;

    //This is used sometimes as well, when we don't need the full course.
    @SerializedName("course_id")
    private String courseId;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }


    @Nullable
    public Course getCourse() {
        return course;
    }

    public String getCourseId() {
        if(courseId == null && course == null) {
            return null;
        } else {
            if(courseId == null) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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

    public Date getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Date lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getLastEditType() {
        return lastEditType;
    }

    public void setLastEditType(String lastEditType) {
        this.lastEditType = lastEditType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.itemId);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeLong(this.startDate != null ? this.startDate.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeString(this.location);
        dest.writeString(this.type);
        dest.writeString(this.lastEditUser);
        dest.writeLong(this.lastEdited != null ? this.lastEdited.getTime() : -1);
        dest.writeString(this.lastEditType);
        dest.writeParcelable(this.course, flags);
    }

    public AgendaItem() {
    }

    private AgendaItem(Parcel in) {
        this.itemId = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        long tmpStartDate = in.readLong();
        this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        this.location = in.readString();
        this.type = in.readString();
        this.lastEditUser = in.readString();
        long tmpLastEdited = in.readLong();
        this.lastEdited = tmpLastEdited == -1 ? null : new Date(tmpLastEdited);
        this.lastEditType = in.readString();
        this.course = in.readParcelable(Course.class.getClassLoader());
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
}
