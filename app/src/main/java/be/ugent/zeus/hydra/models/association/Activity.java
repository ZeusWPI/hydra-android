package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.models.converters.BooleanJsonAdapter;
import be.ugent.zeus.hydra.models.converters.TimeStampDateJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by feliciaan on 27/01/16.
 */
public class Activity implements Parcelable, Serializable {

    private String title;
    @JsonAdapter(TimeStampDateJsonAdapter.class)
    private Date start;
    @JsonAdapter(TimeStampDateJsonAdapter.class)
    private Date end;
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

    protected Activity(Parcel in) {
        title = in.readString();
        start = new Date(in.readLong());
        end = new Date(in.readLong());
        location = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        url = in.readString();
        facebookId = in.readString();
        highlighted = in.readByte() == 1;
        association = in.readParcelable(Association.class.getClassLoader());
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    public Date getStartDate() {
        return start;
    }

    public Date getEndDate() {
        return end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
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
        dest.writeString(title);
        dest.writeLong(start != null ? start.getTime() : 0);
        dest.writeLong(end != null ? end.getTime() : 0);
        dest.writeString(location);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(facebookId);
        dest.writeByte((byte) (highlighted ? 1 : 0));
        dest.writeParcelable(association, flags);

    }
}