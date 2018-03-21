package be.ugent.zeus.hydra.association.event;

import android.os.Parcel;
import android.os.Parcelable;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.common.converter.IntBoolean;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.squareup.moshi.Json;
import java8.util.Objects;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;

import java.io.Serializable;

/**
 * Event from an {@link Association}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public final class Event implements Parcelable, Serializable, Comparable<Event> {

    private String title;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private String location;
    private double latitude;
    private double longitude;
    private String description;
    private String url;
    @Json(name = "facebook_id")
    private String facebookId;
    @IntBoolean
    private boolean highlighted;
    private Association association;

    @SuppressWarnings("unused") // Moshi uses this!
    public Event() {}

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

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public void setEnd(OffsetDateTime end) {
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
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.start));
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.end));
        dest.writeString(this.location);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.facebookId);
        dest.writeByte((byte) (this.highlighted ? 1 : 0));
        dest.writeParcelable(this.association, flags);
    }

    protected Event(Parcel in) {
        this.title = in.readString();
        this.start = DateTypeConverters.toOffsetDateTime(in.readString());
        this.end = DateTypeConverters.toOffsetDateTime(in.readString());
        this.location = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.description = in.readString();
        this.url = in.readString();
        this.facebookId = in.readString();
        this.highlighted = in.readByte() != 0;
        this.association = in.readParcelable(Association.class.getClassLoader());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Double.compare(event.latitude, latitude) == 0 &&
                Double.compare(event.longitude, longitude) == 0 &&
                highlighted == event.highlighted &&
                Objects.equals(title, event.title) &&
                Objects.equals(start, event.start) &&
                Objects.equals(end, event.end) &&
                Objects.equals(location, event.location) &&
                Objects.equals(description, event.description) &&
                Objects.equals(url, event.url) &&
                Objects.equals(facebookId, event.facebookId) &&
                Objects.equals(association, event.association);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, start, end, location, latitude, longitude, description, url, facebookId, highlighted, association);
    }

    /**
     * Calculate an identifier for the event. Since it is calculated with external data, there is no guarantee this
     * will be unique, but it will be very likely.
     *
     * This is conceptually similar to the {@link #hashCode()}, but with a bigger chance of uniqueness, since we use
     * a string.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return title + start.toString() + end.toString() + latitude + longitude + url + association.getName();
    }

    @Override
    public int compareTo(Event o) {
        return start.compareTo(o.start);
    }
}