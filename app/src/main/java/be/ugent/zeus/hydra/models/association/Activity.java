package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import android.os.Parcelable;

import be.ugent.zeus.hydra.models.converters.BooleanJsonAdapter;
import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.TtbUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * Created by feliciaan on 27/01/16.
 */
public class Activity implements Parcelable, Serializable {

    private String title;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime start;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime end;
    private String location;
    private double latitude;
    private double longitude;
    private String description;
    private String url;
    @SerializedName("facebook_id")
    private String facebookId;
    @JsonAdapter(BooleanJsonAdapter.class)
    private boolean highlighted;
    private Association association;

    /**
     * Get the start date, converted to the local time zone. The resulting DateTime is the time as it is used
     * in the current time zone.
     *
     * This value is calculated every time, so if you need it a lot, cache it in a local variable.
     *
     * @return The converted start date.
     */
    public LocalDateTime getLocalStart() {
        return DateUtils.toLocalDateTime(getStart());
    }

    /**
     * Get the end date, converted to the local time zone. The resulting DateTime is the time as it is used
     * in the current time zone.
     *
     * This value is calculated every time, so if you need it a lot, cache it in a local variable.
     *
     * @return The converted end date.
     */
    public LocalDateTime getLocalEnd() {
        return DateUtils.toLocalDateTime(getEnd());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean hasPreciseLocation() {
        return getLatitude() != 0.0 && getLongitude() != 0.0;
    }

    public boolean hasLocation() {
        return getLocation() != null && !getLocation().trim().isEmpty();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public boolean hasUrl() {
        return getUrl() != null && !getUrl().trim().isEmpty();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(TtbUtils.serialize(this.start));
        dest.writeLong(TtbUtils.serialize(this.end));
        dest.writeString(this.location);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.facebookId);
        dest.writeByte(this.highlighted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.association, flags);
    }

    public Activity() {
    }

    protected Activity(Parcel in) {
        this.title = in.readString();
        this.start = TtbUtils.unserialize(in.readLong());
        this.end = TtbUtils.unserialize(in.readLong());
        this.location = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.description = in.readString();
        this.url = in.readString();
        this.facebookId = in.readString();
        this.highlighted = in.readByte() != 0;
        this.association = in.readParcelable(Association.class.getClassLoader());
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel source) {
            return new Activity(source);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };
}