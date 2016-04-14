package be.ugent.zeus.hydra.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by feliciaan on 14/04/16.
 */
public class DateUtils {

    private static SimpleDateFormat WEEKFORMATTER = new SimpleDateFormat("w", new Locale("nl"));
    private static SimpleDateFormat DAYFORMATTER = new SimpleDateFormat("cccc", new Locale("nl"));
    private static DateFormat DATEFORMATTER = SimpleDateFormat.getDateInstance();

    public static String getFriendlyDate(Date date) {
        // TODO: I feel a bit bad about all of this; any good libraries?
        // I couldn't find any that were suitable -- mivdnber
        DateTime today = new DateTime();
        DateTime dateTime = new DateTime(date);
        int thisWeek = Integer.parseInt(WEEKFORMATTER.format(today.toDate()));
        int week = Integer.parseInt(WEEKFORMATTER.format(date));

        int daysBetween = Days.daysBetween(today.toLocalDate(), dateTime.toLocalDate()).getDays();

        if (daysBetween == 0) {
            return "vandaag";
        } else if (daysBetween == 1) {
            return "morgen";
        } else if (daysBetween == 2) {
            return "overmorgen";
        } else if (week == thisWeek || daysBetween < 7) {
            return DAYFORMATTER.format(date).toLowerCase();
        } else if (week == thisWeek + 1) {
            return "volgende " + DAYFORMATTER.format(date).toLowerCase();
        } else {
            return DATEFORMATTER.format(date);
        }
    }
}
