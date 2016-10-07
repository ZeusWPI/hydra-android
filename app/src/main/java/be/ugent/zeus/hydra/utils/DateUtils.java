package be.ugent.zeus.hydra.utils;

import android.content.Context;

import org.threeten.bp.*;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Date utilities.
 *
 * @author feliciaan
 */
public class DateUtils {

    private static Locale locale = new Locale("nl");
    private static DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("w", locale);
    private static DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("cccc", locale);
    private static DateFormat DATE_FORMATTER = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, locale);

    /**
     * Get the date in friendly format.
     */
    public static String getFriendlyDate(LocalDate date) {
        LocalDate today = LocalDate.now();

        int thisWeek = Integer.parseInt(today.format(WEEK_FORMATTER));
        int week = Integer.parseInt(date.format(WEEK_FORMATTER));
        int daysBetween = Period.between(today, date).getDays();

        if (daysBetween == 0) {
            return "vandaag";
        } else if (daysBetween == 1) {
            return "morgen";
        } else if (daysBetween == 2) {
            return "overmorgen";
        } else if (daysBetween < 0) {
            return DATE_FORMATTER.format(DateTimeUtils.toDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else if (daysBetween <= 7) {
            return DAY_FORMATTER.format(date).toLowerCase();
        } else if (week == thisWeek + 1) {
            return "volgende " + DAY_FORMATTER.format(date).toLowerCase();
        } else {
            return DATE_FORMATTER.format(DateTimeUtils.toDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
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