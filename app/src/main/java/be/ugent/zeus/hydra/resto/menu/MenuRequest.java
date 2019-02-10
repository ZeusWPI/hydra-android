package be.ugent.zeus.hydra.resto.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;
import org.threeten.bp.Duration;

import java.util.Locale;

/**
 * Request for the menu's of the resto's.
 *
 * @author mivdnber
 */
public class MenuRequest extends JsonArrayRequest<RestoMenu> {

    @VisibleForTesting
    static final String OVERVIEW_URL = Endpoints.ZEUS_V2 + "resto/menu/%s/overview.json";

    private final SharedPreferences preferences;
    private final Context context;

    public MenuRequest(Context context) {
        super(context, RestoMenu.class);
        this.context = context.getApplicationContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        String resto = RestoPreferenceFragment.getRestoEndpoint(context, preferences);
        return String.format(Locale.ROOT, OVERVIEW_URL, resto);
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofSeconds(10);
    }
}