package be.ugent.zeus.hydra.data.network.requests.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

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

    @VisibleForTesting
    static final String SINT_JAN_URL = Endpoints.ZEUS_RESTO_URL + "menu/nl-sintjansvest/overview.json";
    @VisibleForTesting
    static final String NORMAL_URL = Endpoints.ZEUS_RESTO_URL + "menu/nl/overview.json";

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
            case RestoPreferenceFragment.PREF_RESTO_SINT_JAN:
                return SINT_JAN_URL;
            case RestoPreferenceFragment.PREF_RESTO_NORMAL:
            default:
                return NORMAL_URL;
        }
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR * 12;
    }
}