/*
 * Copyright (c) 2023 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import java.util.Arrays;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.PreferenceFragment;
import be.ugent.zeus.hydra.common.ui.widgets.TimePreference;
import be.ugent.zeus.hydra.common.ui.widgets.TimePreferenceDialogFragmentCompat;
import be.ugent.zeus.hydra.resto.meta.selectable.SelectableMetaViewModel;

/**
 * Preferences for the resto notification.
 *
 * @author Rien Maertens
 * @author Niko Strijbol
 */
public class RestoPreferenceFragment extends PreferenceFragment {
    /**
     * The key of the resto we want to show.
     */
    public static final String PREF_RESTO_KEY = "pref_resto_choice_3";
    /**
     * The name of the chosen resto. We keep this saved, as we then don't have to access to full file every time.
     */
    public static final String PREF_RESTO_NAME = "pref_resto_choice_name";
    
    public static final String DEFAULT_CLOSING_TIME = "21:00";
    public static final String PREF_RESTO_CLOSING_HOUR = "pref_resto_closing_hour";

    public static String getDefaultRestoEndpoint(Context context) {
        return context.getString(R.string.value_resto_default_endpoint);
    }

    public static String getRestoEndpoint(Context context, SharedPreferences preferences) {
        String defaultResto = getDefaultRestoEndpoint(context);
        return preferences.getString(PREF_RESTO_KEY, defaultResto);
    }
    
    public static String getDefaultRestoName(Context context) {
        return context.getString(R.string.resto_default_name);
    }
    
    public static String getRestoName(Context context, SharedPreferences preferences) {
        String defaultName = getDefaultRestoName(context);
        return preferences.getString(PREF_RESTO_NAME, defaultName);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_resto, rootKey);
        requirePreference("pref_choice_resto_select").setVisible(false);

        SelectableMetaViewModel metaViewModel = new ViewModelProvider(this).get(SelectableMetaViewModel.class);
        metaViewModel.data().observe(this, SuccessObserver.with(this::receiveResto));
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(requireContext())
                .setCurrentScreen(requireActivity(), "Settings > Resto", getClass().getSimpleName());
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof TimePreference) {
            DialogFragment f = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getParentFragmentManager(), "time_dialog");
            return;
        }

        super.onDisplayPreferenceDialog(preference);
    }

    // TODO: this is not very clean
    private void receiveResto(@NonNull List<RestoChoice> restos) {
        requirePreference("pref_choice_resto_select_loading").setVisible(false);
        ListPreference selector = requirePreference("pref_choice_resto_select");
        selector.setVisible(true);

        String[] names = restos.stream()
                .map(RestoChoice::name)
                .toArray(String[]::new);
        selector.setEntries(names);
        selector.setEntryValues(names);

        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        String selected = preferences.getString(PREF_RESTO_NAME, getString(R.string.resto_default_name));
        selector.setValue(selected);

        selector.setOnPreferenceChangeListener((preference, newValue) -> {
            String name = (String) newValue;
            int index = Arrays.asList(names).indexOf(name);
            preferences.edit()
                    .putString(PREF_RESTO_NAME, name)
                    .putString(PREF_RESTO_KEY, restos.get(index).endpoint())
                    .apply();
            return true;
        });
    }
}
