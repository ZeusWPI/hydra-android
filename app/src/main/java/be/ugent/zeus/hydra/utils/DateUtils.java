package be.ugent.zeus.hydra.utils;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Date utilities.
 *
 * @author feliciaan
 */
public class DateUtils {

    private static SimpleDateFormat WEEK_FORMATTER = new SimpleDateFormat("w", Locale.getDefault());
    private static SimpleDateFormat DAY_FORMATTER = new SimpleDateFormat("cccc", Locale.getDefault());
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
}
