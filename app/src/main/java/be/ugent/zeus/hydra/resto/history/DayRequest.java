package be.ugent.zeus.hydra.resto.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;
import org.threeten.bp.LocalDate;

import java.util.Locale;


/**
 * Request the menu for a single day.
 *
 * @author Niko Strijbol
 */
public class DayRequest extends JsonSpringRequest<RestoMenu> {

    public static final String OVERVIEW_URL = Endpoints.ZEUS_RESTO_URL + "menu/%s/%d/%d/%d.json";

    private LocalDate date;
    private final SharedPreferences preferences;

    DayRequest(LocalDate date, Context context) {
        super(RestoMenu.class);
        this.date = date;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        String resto = preferences.getString(RestoPreferenceFragment.PREF_RESTO_KEY, RestoPreferenceFragment.PREF_DEFAULT_RESTO);
        return String.format(Locale.US, OVERVIEW_URL, resto, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }
}