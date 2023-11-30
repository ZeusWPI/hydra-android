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

package be.ugent.zeus.hydra.association;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import com.squareup.moshi.Json;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Event from an {@link Association}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
@RecordBuilder
public record Event(
        long id,
        String title,
        @Json(name = "start_time") OffsetDateTime start,
        @Nullable @Json(name = "end_time") OffsetDateTime end,
        @Nullable String location,
        @Nullable String address,
        @Nullable String description,
        @Nullable @Json(name = "infolink") String url,
        String association,
        boolean advertise
) implements Parcelable, Comparable<Event>, EventBuilder.With {

    private Event(Parcel in) {
        this(
                in.readLong(),
                in.readString(),
                DateTypeConverters.toOffsetDateTime(in.readString()),
                DateTypeConverters.toOffsetDateTime(in.readString()),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readInt() == 1
        );
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(DateTypeConverters.fromOffsetDateTime(start));
        dest.writeString(DateTypeConverters.fromOffsetDateTime(end));
        dest.writeString(location);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(association);
        dest.writeInt(advertise ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<>() {
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
    public LocalDateTime localStart() {
        return DateUtils.toLocalDateTime(start());
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
    public LocalDateTime localEnd() {
        if (end == null) {
            return null;
        }
        return DateUtils.toLocalDateTime(end);
    }

    public boolean hasPreciseLocation() {
        return address != null;
    }

    public boolean hasLocation() {
        return location != null && !location.trim().isEmpty();
    }

    public boolean hasUrl() {
        return url != null && !url.trim().isEmpty();
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
    public String identifier() {
        String end = "";
        if (this.end != null) {
            end = this.end.toString();
        }
        return title + start.toString() + end + location + url + association;
    }

    /**
     * Events are compared using their start times.
     */
    @Override
    public int compareTo(Event o) {
        return start.compareTo(o.start);
    }
}
