package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.R;
import org.threeten.bp.*;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.IsoFields;

import java.util.Locale;

/**
 * Date utilities.
 *
 * @author feliciaan
 */
public class DateUtils {

    private static final DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("w");

    private DateUtils() {
        // Utils.
    }

    /**
     * Get the date in friendly format.
     */
    public static String getFriendlyDate(Context context, @NonNull LocalDate date) {
        return getFriendlyDate(context, date, FormatStyle.MEDIUM);
    }

    /**
     * Transform a given date to a more 'friendly' date, with the given formatStyle as a suggestion. This method is very
     * similar to {@link android.text.format.DateUtils#getRelativeTimeSpanString(long)}.
     *
     * The relative date applies to {@code date}s in the future, for the coming two week. The the date is today,
     * tomorrow or overmorrow and the current language supports it, the formatted date will be those terms. If the
     * date is in the current week, the day of week name will be returned. If the date is in the next week,
     * the result will be "next {weekday}".
     *
     * For example, if today is 11/02/2018 and the language supports all features:
     * <ul>
     *     <li>11/02/2018 will result in {@code today}.</li>
     *     <li>12/02/2018 will result in {@code tomorrow}.</li>
     * </ul>
     *
     * Other dates are formatted using {@link #getDateFormatterForStyle(FormatStyle)}.
     *
     * @param date        The date you wish to format.
     * @param formatStyle The style of non-special dates.
     *
     * @return A friendly representation of the date.
     */
    public static String getFriendlyDate(Context context, @NonNull LocalDate date, FormatStyle formatStyle) {

        final int ONE_WEEK_DAYS = 7;
        final int TWO_WEEKS_DAYS = 14;

        LocalDate today = LocalDate.now();
        Locale locale = Locale.getDefault();

        long daysBetween = ChronoUnit.DAYS.between(today, date);

        // Do all the special cases. We currently support three base cases.
        if (daysBetween == 0 && context.getResources().getBoolean(R.bool.date_supports_today)) {
            return context.getString(R.string.date_today);
        } else if (daysBetween == 1 && context.getResources().getBoolean(R.bool.date_supports_tomorrow)) {
            return context.getString(R.string.date_tomorrow);
        } else if (daysBetween == 2 && context.getResources().getBoolean(R.bool.date_supports_overmorrow)) {
            return context.getString(R.string.date_overmorrow);
        } else if (0 <= daysBetween && daysBetween < ONE_WEEK_DAYS) {
            return date.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, locale);
        } else if (0 <= daysBetween && daysBetween < TWO_WEEKS_DAYS && context.getResources().getBoolean(R.bool.date_supports_next)) {
            return context.getString(R.string.date_next_x, date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale));
        } else {
            // All other cases, e.g. the past, the far future or some language that does not support all features.
            return getDateFormatterForStyle(formatStyle).format(date);
        }
    }

    @VisibleForTesting
    static DateTimeFormatter getDateFormatterForStyle(FormatStyle style) {
        return DateTimeFormatter.ofLocalizedDate(style);
    }

    /**
     * Check if for a given date, the {@link #getFriendlyDate(Context, LocalDate)} would return a friendly date or not.
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
     * Convert a date time to a relative string. The precision is one minute, and the resulting string is abbreviated.
     *
     * @param dateTime   The date time.
     * @param context    A context.
     * @param abbreviate If the {@link android.text.format.DateUtils#FORMAT_ABBREV_RELATIVE} flag should be set.
     *
     * @return Relative string
     *
     * @see android.text.format.DateUtils#getRelativeDateTimeString(Context, long, long, long, int)
     */
    public static CharSequence relativeDateTimeString(ZonedDateTime dateTime, Context context, boolean abbreviate) {
        int flags = 0;
        if (abbreviate) {
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
     * Convert a date time to a relative string. The precision is one minute, and the resulting string is abbreviated.
     *
     * @param dateTime   The date time.
     * @param context    A context.
     * @param abbreviate If the {@link android.text.format.DateUtils#FORMAT_ABBREV_RELATIVE} flag should be set.
     *
     * @return Relative string
     *
     * @see android.text.format.DateUtils#getRelativeDateTimeString(Context, long, long, long, int)
     */
    public static CharSequence relativeDateTimeString(OffsetDateTime dateTime, Context context, boolean abbreviate) {
        int flags = 0;
        if (abbreviate) {
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

    public static CharSequence relativeDateTimeString(OffsetDateTime dateTime, Context context) {
        return relativeDateTimeString(dateTime, context, false);
    }

    /**
     * Get the date, converted to the local time zone. The resulting DateTime is the time as it is used in the current
     * time zone.
     *
     * This value is calculated every time, so if you need it a lot, cache it in a local variable.
     *
     * @param dateTime The date time with time zone information
     *
     * @return The converted end date.
     */
    public static LocalDateTime toLocalDateTime(OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String relativeTimeSpan(Context context, OffsetDateTime start, OffsetDateTime end) {
        OffsetDateTime now = OffsetDateTime.now();
        LocalDateTime localStart = DateUtils.toLocalDateTime(start);
        LocalDateTime localEnd = DateUtils.toLocalDateTime(end);

        final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern(context.getString(R.string.formatter_general_hour_only));

        if (start.isBefore(now) && end.isAfter(now)) {
            long epochMillis = end.toInstant().toEpochMilli();
            String endString;
            if (android.text.format.DateUtils.isToday(epochMillis)) {
                endString = localEnd.format(HOUR_FORMATTER);
            } else {
                endString = android.text.format.DateUtils.formatDateTime(
                        context,
                        epochMillis,
                        android.text.format.DateUtils.FORMAT_SHOW_DATE | android.text.format.DateUtils.FORMAT_SHOW_TIME
                );
            }

            return context.getString(R.string.date_now_until, endString);
        }

        if (start.getDayOfMonth() == end.getDayOfMonth()) {
            return context.getString(R.string.date_between, localStart.format(HOUR_FORMATTER), localEnd.format(HOUR_FORMATTER));
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