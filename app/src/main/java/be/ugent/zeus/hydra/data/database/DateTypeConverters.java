package be.ugent.zeus.hydra.data.database;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.OffsetDateTime;
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
    public static DateTimeFormatter OFFSET_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Converts a string representing a date in the format specified by {@link #OFFSET_FORMATTER}.
     *
     * @param sqlValue The string representing the value or {@code null}. The string must be in the format
     *                 specified by {@link #OFFSET_FORMATTER}. If not, the behaviour is undefined.
     *
     * @return The instance or {@code null} if the input was {@code null}.
     */
    @TypeConverter
    public static OffsetDateTime toOffsetDateTime(String sqlValue) {
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
    @TypeConverter
    public static String fromOffsetDateTime(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return dateTime.format(OFFSET_FORMATTER);
        }
    }
}