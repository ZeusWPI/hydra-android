package be.ugent.zeus.hydra.resto.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;
import org.threeten.bp.LocalDate;

/**
 * @author Niko Strijbol
 */
public class SingleDayLiveData extends RequestLiveData<RestoMenu> implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SingleDayLiveData(Context context) {
        super(context, new DayRequest(), false);
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

    public void changeDate(LocalDate newDate) {
        // This is due to Java limitations
        getRequest().setDate(newDate);
        if (getRequest().isSetup()) {
            loadData(Bundle.EMPTY);
        }
    }

    public void changeResto(RestoChoice choice) {
        getRequest().setChoice(choice);
        if (getRequest().isSetup()) {
            loadData(Bundle.EMPTY);
        }
    }

    @Override
    protected DayRequest getRequest() {
        return (DayRequest) super.getRequest();
    }
}