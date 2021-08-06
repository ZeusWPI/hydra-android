/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    private long id;
    private String title;
    @Json(name = "start_time")
    private OffsetDateTime start;
    @Json(name = "end_time")
    private OffsetDateTime end;
    private String location;
    private String address;
    private String description;
    @Json(name = "infolink")
    private String url;
    private String association;
    private boolean advertise;

    public Event() {
        // Moshi uses this!
    }

    protected Event(Parcel in) {
        id = in.readLong();
        title = in.readString();
        location = in.readString();
        address = in.readString();
        description = in.readString();
        url = in.readString();
        association = in.readString();
        advertise = in.readInt() == 1;
        start = end = DateTypeConverters.toOffsetDateTime(in.readString());
        end = DateTypeConverters.toOffsetDateTime(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(association);
        dest.writeInt(advertise ? 1 : 0);
        dest.writeString(DateTypeConverters.fromOffsetDateTime(start));
        dest.writeString(DateTypeConverters.fromOffsetDateTime(end));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    /**
     * Get the start date, converted to the local time zone. The resulting DateTime is the time as it is used
     * in the current time zone.
     * <p>
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
     * <p>
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

    public String getAddress() {
        return address;
    }

    public boolean hasPreciseLocation() {
        return getAddress() != null;
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

    public String getAssociation() {
        return association;
    }

    /**
     * Calculate an identifier for the event. Since it is calculated with external data, there is no guarantee this
     * will be unique, but it will be very likely.
     * <p>
     * This is conceptually similar to the {@link #hashCode()}, but with a bigger chance of uniqueness, since we use
     * a string.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return title + start.toString() + end.toString() + location + url + association;
    }

    /**
     * Events are compared using their start times.
     */
    @Override
    public int compareTo(Event o) {
        return start.compareTo(o.start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
