package be.ugent.zeus.hydra.models.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;

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
     * Filter the resto menu's. After 20:00, the menu of today is removed from the list. This will also remove resto's
     * that are not today.
     *
     * @param data The original data, probably from the server.
     * @return The filtered data.
     */
    public static ArrayList<RestoMenu> filter(ArrayList<RestoMenu> data, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        LocalTime closingHour = LocalTime.parse(preferences.getString(PREF_RESTO_CLOSING_HOUR, DEFAULT_CLOSING_TIME));

        ArrayList<RestoMenu> list = new ArrayList<>();

        final LocalDate today = LocalDate.now();
        final boolean isTooLate = LocalDateTime.now().isAfter(LocalDateTime.of(LocalDate.now(), closingHour));

        //we still DONT have filters
        for(RestoMenu menu: data) {
            //Menu is skipped if (it is before today) or (it is too late and it is today)
            if(!menu.getDate().isBefore(today) && (!isTooLate || !menu.getDate().isEqual(today))) {
                list.add(menu);
            }
        }

        return list;
    }
}