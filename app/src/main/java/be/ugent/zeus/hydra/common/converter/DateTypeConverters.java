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

package be.ugent.zeus.hydra.common.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.*;
import java.time.format.DateTimeFormatter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * Converts for various date-related classes.
 *
 * @author Niko Strijbol
 */
public class DateTypeConverters {

    /**
     * The format of the result of {@link #fromOffsetDateTime(OffsetDateTime)} and the format expected by
     * {@link #toOffsetDateTime(String)}.
     */
    static final DateTimeFormatter OFFSET_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private DateTypeConverters() {
    }

    /**
     * Converts a string representing a date in the format specified by {@link #OFFSET_FORMATTER}.
     *
     * @param sqlValue The string representing the value or {@code null}. The string must be in the format
     *                 specified by {@link #OFFSET_FORMATTER}. If not, the behaviour is undefined.
     * @return The instance or {@code null} if the input was {@code null}.
     */
    @Nullable
    @TypeConverter
    public static OffsetDateTime toOffsetDateTime(@Nullable String sqlValue) {
        if (sqlValue == null) {
            return null;
        } else {
            return OffsetDateTime.parse(sqlValue, OFFSET_FORMATTER);
        }
    }

    /**
     * Converts a offset date time to a string in the format specified by {@link #OFFSET_FORMATTER}.
     *
     * @param dateTime The date time or {@code null}.
     * @return The string or {@code null} if the input was {@code null}.
     */
    @Nullable
    @TypeConverter
    public static String fromOffsetDateTime(@Nullable OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return dateTime.format(OFFSET_FORMATTER);
        }
    }

    @Nullable
    @TypeConverter
    public static String fromInstant(Instant instant) {
        if (instant == null) {
            return null;
        } else {
            return instant.toString();
        }
    }

    @Nullable
    @TypeConverter
    public static Instant toInstant(String value) {
        if (value == null) {
            return null;
        } else {
            return Instant.parse(value);
        }
    }

    private static String fromLocalZonedDateTime(@LocalZonedDateTime ZonedDateTime zonedDateTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(zonedDateTime.toInstant(), LocalZonedDateTime.BRUSSELS);
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @LocalZonedDateTime
    private static ZonedDateTime toLocalZonedDateTime(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(LocalZonedDateTime.BRUSSELS);
    }

    public static class GsonOffset {

        @FromJson
        OffsetDateTime read(String value) {
            return toOffsetDateTime(value);
        }

        @ToJson
        String write(OffsetDateTime offsetDateTime) {
            return fromOffsetDateTime(offsetDateTime);
        }
    }

    public static class LocalZonedDateTimeInstance {

        @FromJson
        @LocalZonedDateTime
        ZonedDateTime fromJson(String value) {
            return toLocalZonedDateTime(value);
        }

        @ToJson
        String toJson(@LocalZonedDateTime ZonedDateTime zonedDateTime) {
            return fromLocalZonedDateTime(zonedDateTime);
        }
    }
}
