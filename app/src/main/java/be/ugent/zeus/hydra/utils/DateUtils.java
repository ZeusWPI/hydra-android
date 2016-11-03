package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import org.threeten.bp.*;
import org.threeten.bp.chrono.Chronology;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.format.FormatStyle;

import java.util.Locale;

/**
 * Date utilities.
 *
 * @author feliciaan
 */
public class DateUtils {

    private static Locale locale = new Locale("nl");
    private static DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("w", locale);

    //Package private formatters for test.
    @VisibleForTesting
    static DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("cccc", locale);
    @VisibleForTesting
    static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);

    /**
     * Get the date in friendly format.
     */
    public static String getFriendlyDate(@NonNull LocalDate date) {
        LocalDate today = LocalDate.now();

        int thisWeek = Integer.parseInt(today.format(WEEK_FORMATTER));
        int week = Integer.parseInt(date.format(WEEK_FORMATTER));
        int daysBetween = Period.between(today, date).getDays();

        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.MEDIUM, FormatStyle.MEDIUM, Chronology.ofLocale(locale), locale);

        if (daysBetween == 0) {
            return "vandaag";
        } else if (daysBetween == 1) {
            return "morgen";
        } else if (daysBetween == 2) {
            return "overmorgen";
        } else if (daysBetween < 0) {
            return DATE_FORMATTER.format(date);
        } else if (daysBetween < 7) {
            return DAY_FORMATTER.format(date).toLowerCase();
        } else if (week == thisWeek + 1) {
            return "volgende " + DAY_FORMATTER.format(date).toLowerCase();
        } else {
            return DATE_FORMATTER.format(date);
        }
    }

    /**
     * Convert a date time to a relative string. The precision is one minute, and the resulting string is
     * abbreviated.
     *
     * @see android.text.format.DateUtils#getRelativeDateTimeString(Context, long, long, long, int)
     *
     * @param dateTime The date time.
     * @param context A context.
     * @param abbreviate If the {@link android.text.format.DateUtils#FORMAT_ABBREV_RELATIVE} flag should be set.
     *
     * @return Relative string
     */
    public static CharSequence relativeDateTimeString(ZonedDateTime dateTime, Context context, boolean abbreviate) {
        int flags = 0;
        if(abbreviate) {
            flags = android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;
        }
        return android.text.format.DateUtils.getRelativeDateTimeString(
                context,
                dateTime.toInstant().toEpochMilli(),
                android.text.format.DateUtils.MINUTE_IN_MILLIS,
                android.text.format.DateUtils.WEEK_IN_MILLIS,
                flags
        );
    }

    public static CharSequence relativeDateTimeString(ZonedDateTime dateTime, Context context) {
        return relativeDateTimeString(dateTime, context, false);
    }

    /**
     * Get the date, converted to the local time zone. The resulting DateTime is the time as it is used
     * in the current time zone.
     *
     * This value is calculated every time, so if you need it a lot, cache it in a local variable.
     *
     * @param dateTime The date time with time zone information
     *
     * @return The converted end date.
     */
    public static LocalDateTime toLocalDateTime(final ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
}