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

package be.ugent.zeus.hydra.common.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import be.ugent.zeus.hydra.R;

/**
 * Date utilities.
 *
 * @author feliciaan
 */
public class DateUtils {

    private DateUtils() {
        // Utils.
    }

    /**
     * Get the date in friendly format. The style is {@link FormatStyle#MEDIUM}.
     *
     * @see #friendlyDate(Context, LocalDate, FormatStyle)
     */
    public static String friendlyDate(Context context, @NonNull LocalDate date) {
        return friendlyDate(context, date, FormatStyle.MEDIUM);
    }

    /**
     * Transform a given date to a more 'friendly' date, with the given formatStyle as a suggestion. This method is very
     * similar to {@link android.text.format.DateUtils#getRelativeTimeSpanString(long)}.
     * <p>
     * The relative date applies to {@code date}s in the future, for the coming two week. If the date is today,
     * tomorrow or overmorrow and the current language supports it, the formatted date will be those terms. If the
     * date is in the current week, the day of week name (e.g. Monday) will be returned. If the date is in the next week,
     * the result will be "next {weekday}".
     * <p>
     * For example, if today is 11/02/2018 and the language supports all features:
     * <ul>
     *     <li>11/02/2018 will result in {@code today}.</li>
     *     <li>12/02/2018 will result in {@code tomorrow}.</li>
     * </ul>
     * <p>
     * A date is thus considered special if it is one of the cases above; this can be summarized as follows: a date
     * is special if it is less than 14 days (two weeks) in the future.
     * <p>
     * Other dates are formatted using {@link #dateFormatterForStyle(FormatStyle)}.
     *
     * @param date        The date you wish to format.
     * @param formatStyle The style of non-special dates.
     * @return A friendly representation of the date.
     */
    public static String friendlyDate(Context context, @NonNull LocalDate date, FormatStyle formatStyle) {

        int ONE_WEEK_DAYS = 7;
        int TWO_WEEKS_DAYS = 14;

        LocalDate today = LocalDate.now();
        Locale locale = Locale.getDefault();

        long daysBetween = ChronoUnit.DAYS.between(today, date);

        // We currently support three specialized cases.
        if (daysBetween == 0 && context.getResources().getBoolean(R.bool.date_supports_today)) {
            return context.getString(R.string.date_today);
        } else if (daysBetween == 1 && context.getResources().getBoolean(R.bool.date_supports_tomorrow)) {
            return context.getString(R.string.date_tomorrow);
        } else if (daysBetween == 2 && context.getResources().getBoolean(R.bool.date_supports_overmorrow)) {
            return context.getString(R.string.date_overmorrow);
        } else if (0 <= daysBetween && daysBetween < ONE_WEEK_DAYS) {
            return date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        } else if (0 <= daysBetween && daysBetween < TWO_WEEKS_DAYS) {
            return context.getString(R.string.date_next_x, date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale));
        } else {
            // All other cases, e.g. the past, the far future or some language that does not support all features.
            return dateFormatterForStyle(formatStyle).format(date);
        }
    }

    @VisibleForTesting
    static DateTimeFormatter dateFormatterForStyle(FormatStyle style) {
        return DateTimeFormatter.ofLocalizedDate(style);
    }

    /**
     * Check if a given date is friendly or not. Friendly is defined by {@link #friendlyDate(Context, LocalDate)}.
     *
     * @param date The date to check.
     * @return True if a friendly date would be returned.
     */
    static boolean willBeFriendly(@NonNull LocalDate date) {
        long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), date);
        return 0 <= daysBetween && daysBetween < 14;
    }

    /**
     * Convert a date time to a relative string. The precision is one minute, and the resulting string is abbreviated.
     *
     * @param dateTime   The date time.
     * @param context    A context.
     * @param abbreviate If the {@link android.text.format.DateUtils#FORMAT_ABBREV_RELATIVE} flag should be set.
     * @return Relative string
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

    public static CharSequence relativeDateTimeString(OffsetDateTime dateTime, Context context) {
        return relativeDateTimeString(dateTime, context, false);
    }

    public static String friendlyDateTime(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    /**
     * Get the date, converted to the local time zone. The resulting DateTime is the time as it is used in the current
     * time zone.
     * <p>
     * This value is calculated every time, so if you need it a lot, cache it in a local variable.
     *
     * @param dateTime The date time with time zone information
     * @return The converted end date.
     */
    public static LocalDateTime toLocalDateTime(OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
}
