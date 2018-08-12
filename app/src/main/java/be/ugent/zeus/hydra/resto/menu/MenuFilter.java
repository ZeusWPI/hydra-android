package be.ugent.zeus.hydra.resto.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;
import java9.util.function.Function;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.List;

/**
 * Filters a list of menu's to only retain the useful ones.
 *
 * @author Niko Strijbol
 */
public class MenuFilter implements Function<List<RestoMenu>, List<RestoMenu>> {

    private final SharedPreferences preferences;
    private final Clock clock;

    public MenuFilter(Context context) {
        this(context, Clock.systemDefaultZone());
    }

    @VisibleForTesting
    MenuFilter(Context context, Clock clock) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.clock = clock;
    }

    @VisibleForTesting
    MenuFilter(SharedPreferences preferences, Clock clock) {
        this.preferences = preferences;
        this.clock = clock;
    }

    /**
     * Filter the menus. Note that the current time can be set using {@link #clock} (useful for testing).
     * The filter will act as follows:
     *
     * <ul>
     *     <li>If the menu's date is before today, the menu will be removed. (menu.date < now)</li>
     *     <li>If the menu's date is after today, the menu will be retained. (menu.date > now)</li>
     *     <li>If the menu's date is the same as today's date, the filtering depends on the user option
     *     {@link RestoPreferenceFragment#PREF_RESTO_CLOSING_HOUR}. The default value is {@link RestoPreferenceFragment#DEFAULT_CLOSING_TIME}.
     *     <ul>
     *         <li>If the current time is before the time in the settings, the menu will be retained.</li>
     *         <li>If the current time is equal to or after the time set in the settings, the menu will be removed.</li>
     *     </ul>
     *     </li>
     * </ul>
     *
     * @param restoMenus The menus to filter. Cannot be null.
     *
     * @return The filtered menus. Will not be null.
     */
    @NonNull
    @Override
    public List<RestoMenu> apply(@NonNull List<RestoMenu> restoMenus) {
        LocalTime closingHour = LocalTime.parse(
                preferences.getString(
                        RestoPreferenceFragment.PREF_RESTO_CLOSING_HOUR,
                        RestoPreferenceFragment.DEFAULT_CLOSING_TIME
                )
        );
        LocalDate today = LocalDate.now(clock);
        boolean isEarlyEnough = LocalDateTime.now(clock).isBefore(LocalDateTime.of(today, closingHour));

        return StreamSupport.stream(restoMenus)
                .filter(m -> m.getDate().isAfter(today) || (m.getDate().isEqual(today) && isEarlyEnough))
                .collect(Collectors.toList());
    }
}