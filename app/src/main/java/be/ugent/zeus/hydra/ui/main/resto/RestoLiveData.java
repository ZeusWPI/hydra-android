package be.ugent.zeus.hydra.ui.main.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.requests.resto.CurrentMenuFilter;
import be.ugent.zeus.hydra.data.network.requests.resto.MenuFilter;
import be.ugent.zeus.hydra.data.network.requests.resto.MenuRequest;
import be.ugent.zeus.hydra.data.network.requests.resto.SelectableMetaRequest;
import be.ugent.zeus.hydra.repository.data.RequestLiveData;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;

import java.util.Arrays;

/**
 * @author Niko Strijbol
 */
public class RestoLiveData extends RequestLiveData<RestoMenu> implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SelectableMetaRequest.RestoChoice previousChoice;

    public RestoLiveData(Context context) {
        super(context, Requests.mapE(Requests.map(
                Requests.map(Requests.cache(context, new MenuRequest(context)), Arrays::asList),
                new MenuFilter(context)
        ), new CurrentMenuFilter()));
    }

    @Override
    protected void onActive() {
        super.onActive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String key = preferences.getString(RestoPreferenceFragment.PREF_RESTO_KEY, RestoPreferenceFragment.PREF_DEFAULT_RESTO);
        String name = preferences.getString(RestoPreferenceFragment.PREF_RESTO_NAME, getContext().getString(R.string.resto_default_name));
        SelectableMetaRequest.RestoChoice resto = new SelectableMetaRequest.RestoChoice(name, key);
        // Register the listener for when the settings change while it's active
        preferences.registerOnSharedPreferenceChangeListener(this);
        // Check if the value is equal to the saved value. If not, we need to reload.
        if (previousChoice != null && !resto.equals(previousChoice)) {
            loadData(Bundle.EMPTY);
        }
        previousChoice = resto;
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (RestoPreferenceFragment.PREF_RESTO_KEY.equals(key) || RestoPreferenceFragment.PREF_RESTO_NAME.equals(key)) {
            loadData(Bundle.EMPTY);
        }
    }
}