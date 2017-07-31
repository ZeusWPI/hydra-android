package be.ugent.zeus.hydra.data.network.requests.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.repository.Cache;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;

/**
 * Request for the menu's of the resto's.
 *
 * @author mivdnber
 */
public class MenuRequest extends JsonSpringRequest<RestoMenu[]> implements CacheableRequest<RestoMenu[]> {

    private final Context context;

    public MenuRequest(Context context) {
        super(RestoMenu[].class);
        this.context = context.getApplicationContext(); //Prevent leak
    }

    @NonNull
    @Override
    public String getCacheKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String resto = preferences.getString(RestoPreferenceFragment.PREF_RESTO, RestoPreferenceFragment.PREF_DEFAULT_RESTO);
        return "menuOverview_" + resto + ".json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String resto = preferences.getString(RestoPreferenceFragment.PREF_RESTO, RestoPreferenceFragment.PREF_DEFAULT_RESTO);

        switch (resto) {
            case "1":
                return Endpoints.ZEUS_RESTO_URL + "menu/nl-sintjansvest/overview.json";
            case "0":
            default:
                return Endpoints.ZEUS_RESTO_URL + "menu/nl/overview.json";
        }
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR * 12;
    }
}