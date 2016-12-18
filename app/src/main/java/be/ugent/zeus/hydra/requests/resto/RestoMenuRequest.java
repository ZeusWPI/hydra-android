package be.ugent.zeus.hydra.requests.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.fragments.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * CacheRequest for an overview of the resto menu.
 *
 * @author mivdnber
 */
public class RestoMenuRequest extends CacheableRequest<RestoOverview> {

    private final Context context;

    public RestoMenuRequest(Context context) {
        super(RestoOverview.class);
        this.context = context.getApplicationContext(); //Prevent leak
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "menuOverview.json";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String resto = preferences.getString(RestoPreferenceFragment.PREF_RESTO, RestoPreferenceFragment.PREF_DEFAULT_RESTO);

        switch (resto) {
            case "1":
                return ZEUS_API_URL + "2.0/resto/menu/nl-sintjansvest/overview.json";
            case "0":
            default:
                return ZEUS_API_URL + "2.0/resto/menu/nl/overview.json";
        }
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR * 12;
    }
}