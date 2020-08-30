package be.ugent.zeus.hydra.association.event;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import com.squareup.moshi.Json;

/**
 * Event from an {@link Association}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public final class Event implements Parcelable, Comparable<Event> {

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
    private String title;
    private OffsetDateTime start;
    @Nullable
    private OffsetDateTime end;
    private String location;
    private double latitude;
    private double longitude;
    private String description;
    private String url;
    private Association association;
    @Json(name = "facebook_id")
    private String facebookId;

    public Event() {
        // Moshi uses this!
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
        this.association = in.readParcelable(Association.class.getClassLoader());
    }

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
    @Nullable
    public LocalDateTime getLocalEnd() {
        if (getEnd() == null) {
            return null;
        }
        return DateUtils.toLocalDateTime(getEnd());
    }

    public String getTitle() {
        return title;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    @Nullable
    public OffsetDateTime getEnd() {
        return end;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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

    public String getUrl() {
        return url;
    }

    public boolean hasUrl() {
        return getUrl() != null && !getUrl().trim().isEmpty();
    }

    public Association getAssociation() {
        return association;
    }

    public String getFacebookId() {
        return facebookId;
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
        dest.writeParcelable(this.association, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Double.compare(event.latitude, latitude) == 0 &&
                Double.compare(event.longitude, longitude) == 0 &&
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
        return Objects.hash(title, start, end, location, latitude, longitude, description, url, association, facebookId);
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
        String endDate = end == null ? "" : end.toString();
        return title + start.toString() + endDate + latitude + longitude + url + association.getName();
    }

    /**
     * Events are compared using their start times.
     */
    @Override
    public int compareTo(Event o) {
        return start.compareTo(o.start);
    }
}
