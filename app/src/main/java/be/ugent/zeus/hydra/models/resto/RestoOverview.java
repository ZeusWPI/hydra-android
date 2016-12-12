package be.ugent.zeus.hydra.models.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java8.util.stream.Stream;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * The over class. This class contains a list of menu's.
 *
 * Since this a is a simple wrapper for gson, this does not need to be Parcelable.
 *
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoOverview extends ArrayList<RestoMenu> {

    public static final String DEFAULT_CLOSING_TIME = "21:00";
    public static final String PREF_RESTO_CLOSING_HOUR = "pref_resto_closing_hour";

    /**
     * Filter the resto menu's. After 21:00, the menu of today is removed from the list. This will also remove resto's
     * that are not today. Filtering happens in place.
     *
     * @param data The data to filter.
     */
    public static void filter(RestoOverview data, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        LocalTime closingHour = LocalTime.parse(preferences.getString(PREF_RESTO_CLOSING_HOUR, DEFAULT_CLOSING_TIME));

        final LocalDate today = LocalDate.now();
        final boolean isTooLate = LocalDateTime.now().isAfter(LocalDateTime.of(LocalDate.now(), closingHour));

        ListIterator<RestoMenu> it = data.listIterator();
        while(it.hasNext()) {
            RestoMenu next = it.next();
            if(next.getDate().isBefore(today) || (isTooLate && next.getDate().isEqual(today))) {
                it.remove();
            }
        }
    }

    /**
     * Filter the resto menu's. This will remove resto's that are before today, or are today but are after the closing
     * time.
     *
     * @param data The data to filter.
     */
    public static Stream<RestoMenu> filter(Stream<RestoMenu> data, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        LocalTime closingHour = LocalTime.parse(preferences.getString(PREF_RESTO_CLOSING_HOUR, DEFAULT_CLOSING_TIME));
        LocalDate today = LocalDate.now();
        boolean isEarlyEnough = LocalDateTime.now().isBefore(LocalDateTime.of(LocalDate.now(), closingHour));

        return data.filter(m -> m.getDate().isAfter(today) || (m.getDate().isEqual(today) && isEarlyEnough));
    }
}