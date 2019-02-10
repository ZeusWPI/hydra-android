package be.ugent.zeus.hydra.common.converter;

import androidx.room.TypeConverter;
import androidx.annotation.Nullable;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

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
     *
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
     *
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

    public static class GsonInstant {

        @FromJson
        Instant read(String value) {
            return toInstant(value);
        }

        @ToJson
        String write(Instant value) {
            return fromInstant(value);
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