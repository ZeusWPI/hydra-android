package be.ugent.zeus.hydra.models.minerva;

import android.os.Parcel;
import android.os.Parcelable;

import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.TtbUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * Created by feliciaan on 29/06/16.
 */
public class Announcement implements Serializable, Parcelable {

    private String title;
    private String content;
    @SerializedName("email_sent")
    private boolean emailSent;
    @SerializedName("item_id")
    private int itemId;
    @SerializedName("last_edit_user")
    private String lecturer;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    @SerializedName("last_edit_time")
    private ZonedDateTime minervaDate;

    private ZonedDateTime read;
    private Course course;

    public boolean isRead() {
        return read != null;
    }

    public void setRead(ZonedDateTime read) {
        this.read = read;
    }

    public ZonedDateTime getRead() {
        return read;
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

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public ZonedDateTime getDate() {
        return this.minervaDate;
    }

    public void setDate(ZonedDateTime date) {
        this.minervaDate = date;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeByte(this.emailSent ? (byte) 1 : (byte) 0);
        dest.writeInt(this.itemId);
        dest.writeString(this.lecturer);
        dest.writeLong(TtbUtils.serialize(this.minervaDate));
        dest.writeLong(TtbUtils.serialize(this.read));
        dest.writeSerializable(this.course);
    }

    public Announcement() {
    }

    protected Announcement(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.emailSent = in.readByte() != 0;
        this.itemId = in.readInt();
        this.lecturer = in.readString();
        long tmp = in.readLong();
        this.minervaDate = TtbUtils.unserialize(tmp);
        long tmpRead = in.readLong();
        this.read = TtbUtils.unserialize(tmpRead);
        this.course = (Course) in.readSerializable();
    }

    public static final Parcelable.Creator<Announcement> CREATOR = new Parcelable.Creator<Announcement>() {
        @Override
        public Announcement createFromParcel(Parcel source) {
            return new Announcement(source);
        }

        @Override
        public Announcement[] newArray(int size) {
            return new Announcement[size];
        }
    };
}
