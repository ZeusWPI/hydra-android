package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.IsoFields;

import java.util.Locale;

/**
 * Date utilities.
 *
 * @author feliciaan
 */
public class DateUtils {

    private static Locale locale = new Locale("nl");
    private static DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("w", locale);
    private static DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", new Locale("nl"));

    //Package private formatters for test.
    @VisibleForTesting
    static DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("cccc", locale);
    @VisibleForTesting
    static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);

    @VisibleForTesting
    static DateTimeFormatter LONG_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale);

    /**
     * Get the date in friendly format.
     */
    public static String getFriendlyDate(@NonNull LocalDate date) {
        return getFriendlyDate(date, DATE_FORMATTER);
    }

    public static String getLongFriendlyDate(@NonNull LocalDate date) {
        return getFriendlyDate(date, LONG_DATE_FORMATTER);
    }

    public static String getFriendlyDate(@NonNull LocalDate date, DateTimeFormatter dateFormatter) {
        LocalDate today = LocalDate.now();

        int thisWeek = Integer.parseInt(today.format(WEEK_FORMATTER));
        int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        long daysBetween = ChronoUnit.DAYS.between(today, date);

        if (daysBetween == 0) {
            return "vandaag";
        } else if (daysBetween == 1) {
            return "morgen";
        } else if (daysBetween == 2) {
            return "overmorgen";
        } else if (daysBetween < 0) {
            return dateFormatter.format(date);
        } else if (daysBetween < 7) {
            return DAY_FORMATTER.format(date).toLowerCase();
        } else if (week == thisWeek + 1) {
            return "volgende " + DAY_FORMATTER.format(date).toLowerCase();
        } else {
            return dateFormatter.format(date);
        }
    }

    /**
     * Check if for a given date, the {@link #getFriendlyDate(LocalDate)} would return a friendly date or not.
     *
     * @param date The date to check.
     *
     * @return True if a friendly date would be returned.
     */
    public static boolean isFriendly(@NonNull LocalDate date) {
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, date);
        int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int thisWeek = Integer.parseInt(today.format(WEEK_FORMATTER));
        return daysBetween == 0 || daysBetween == 1 || daysBetween == 2 || daysBetween >= 0 && (daysBetween < 7 || week == thisWeek + 1);
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

    /**
     * Get a relative date string for a start and stop date. The string accounts for events that are on the same day,
     * by not showing the day twice for example.
     *
     * @param start The start date. Should be before the end date.
     * @param end The end date.
     * @return The string.
     */
    public static String relativeTimeSpan(Context context, ZonedDateTime start, ZonedDateTime end) {

        ZonedDateTime now = ZonedDateTime.now();

        LocalDateTime localStart = DateUtils.toLocalDateTime(start);
        LocalDateTime localEnd = DateUtils.toLocalDateTime(end);

        if (start.isBefore(now) && end.isAfter(now)) {
            String endString;
            if (android.text.format.DateUtils.isToday(end.toInstant().toEpochMilli())) {
                endString = localEnd.format(HOUR_FORMATTER);
            } else {
                endString = android.text.format.DateUtils.formatDateTime(
                        context,
                        end.toInstant().toEpochMilli(),
                        android.text.format.DateUtils.FORMAT_SHOW_DATE | android.text.format.DateUtils.FORMAT_SHOW_TIME
                );
            }

            return "Nu tot " + endString;
        }


        if (start.getDayOfMonth() == end.getDayOfMonth()) {
            return localStart.format(HOUR_FORMATTER) + " tot " + localEnd.format(HOUR_FORMATTER);
        } else {
            return android.text.format.DateUtils.formatDateRange(
                    context,
                    start.toInstant().toEpochMilli(),
                    end.toInstant().toEpochMilli(),
                    android.text.format.DateUtils.FORMAT_SHOW_DATE | android.text.format.DateUtils.FORMAT_SHOW_TIME
            );
        }
    }
}