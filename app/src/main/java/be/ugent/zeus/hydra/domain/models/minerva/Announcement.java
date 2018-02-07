package be.ugent.zeus.hydra.domain.models.minerva;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.data.dto.DateTypeConverters;
import java8.util.Objects;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import java.io.Serializable;

/**
 * Minerva announcement model class.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public final class Announcement implements Serializable, Parcelable {

    private int itemId;
    private String title;
    private String content;
    private boolean emailSent;
    private String lecturer;
    private OffsetDateTime date;
    private Instant read;
    private Course course;

    public Announcement() {
        //No-args constructor
    }

    /**
     * @return True if the announcement has been read.
     */
    public boolean isRead() {
        return read != null;
    }

    /**
     * Set the moment the announcement was read. To persist, use {@link be.ugent.zeus.hydra.domain.repository.AnnouncementRepository}.
     *
     * @param read The date or null to set to unread.
     */
    public void setRead(Instant read) {
        this.read = read;
    }

    /**
     * Get the date this announcement was read or null if not read.
     *
     * @return The date or null.
     */
    @Nullable
    public Instant getRead() {
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

    public OffsetDateTime getDate() {
        return this.date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
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
        dest.writeByte((byte) (this.emailSent ? 1 : 0));
        dest.writeInt(this.itemId);
        dest.writeString(this.lecturer);
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.date));
        dest.writeString(DateTypeConverters.fromInstant(this.read));
        dest.writeParcelable(this.course, flags);
    }

    protected Announcement(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.emailSent = in.readByte() != 0;
        this.itemId = in.readInt();
        this.lecturer = in.readString();
        this.date = DateTypeConverters.toOffsetDateTime(in.readString());
        this.read = DateTypeConverters.toInstant(in.readString());
        this.course = in.readParcelable(Course.class.getClassLoader());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Announcement that = (Announcement) o;
        return itemId == that.itemId &&
                Objects.equals(read, that.read);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, read);
    }
}
