package be.ugent.zeus.hydra.utils;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date utilities.
 *
 * @author feliciaan
 */
public class DateUtils {

    private static Locale locale = new Locale("nl");
    private static SimpleDateFormat WEEK_FORMATTER = new SimpleDateFormat("w", locale);
    private static SimpleDateFormat DAY_FORMATTER = new SimpleDateFormat("cccc", locale);
    private static DateFormat DATE_FORMATTER = SimpleDateFormat.getDateInstance();

    /**
     * Get the date in friendly format.
     */
    public static String getFriendlyDate(Date date) {
        DateTime today = new DateTime();
        DateTime dateTime = new DateTime(date);
        int thisWeek = Integer.parseInt(WEEK_FORMATTER.format(today.toDate()));
        int week = Integer.parseInt(WEEK_FORMATTER.format(date));

        int daysBetween = Days.daysBetween(today.toLocalDate(), dateTime.toLocalDate()).getDays();

        if (daysBetween == 0) {
            return "vandaag";
        } else if (daysBetween == 1) {
            return "morgen";
        } else if (daysBetween == 2) {
            return "overmorgen";
        } else if (daysBetween < 0) {
            return DATE_FORMATTER.format(date);
        } else if (daysBetween <= 7) {
            return DAY_FORMATTER.format(date).toLowerCase();
        } else if (week == thisWeek + 1) {
            return "volgende " + DAY_FORMATTER.format(date).toLowerCase();
        } else {
            return DATE_FORMATTER.format(date);
        }
    }

    public static CharSequence relativeDateString(Date date, Context context) {
        return android.text.format.DateUtils.getRelativeDateTimeString(context, date.getTime(), android.text.format.DateUtils.MINUTE_IN_MILLIS, android.text.format.DateUtils.WEEK_IN_MILLIS, 0);
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