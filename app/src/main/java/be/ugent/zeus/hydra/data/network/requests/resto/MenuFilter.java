package be.ugent.zeus.hydra.data.network.requests.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class MenuFilter implements Function<List<RestoMenu>, List<RestoMenu>> {

    private final SharedPreferences preferences;

    public MenuFilter(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public List<RestoMenu> apply(List<RestoMenu> restoMenus) {
        LocalTime closingHour = LocalTime.parse(
                preferences.getString(
                        RestoPreferenceFragment.PREF_RESTO_CLOSING_HOUR,
                        RestoPreferenceFragment.DEFAULT_CLOSING_TIME
                )
        );
        LocalDate today = LocalDate.now();
        boolean isEarlyEnough = LocalDateTime.now().isBefore(LocalDateTime.of(LocalDate.now(), closingHour));

        return StreamSupport.stream(restoMenus)
                .filter(m -> m.getDate().isAfter(today) || (m.getDate().isEqual(today) && isEarlyEnough))
                .collect(Collectors.toList());
    }
}