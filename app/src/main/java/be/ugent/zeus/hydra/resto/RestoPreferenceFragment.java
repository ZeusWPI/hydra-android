/*
 * Copyright (c) 2021 The Hydra authors
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
 */
public class RestoPreferenceFragment extends PreferenceFragment {
    public static final String DEFAULT_CLOSING_TIME = "21:00";
    public static final String PREF_RESTO_CLOSING_HOUR = "pref_resto_closing_hour";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_resto, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(requireContext())
                .setCurrentScreen(requireActivity(), "Settings > Resto", getClass().getSimpleName());
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TimePreference) {
            DialogFragment f = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getParentFragmentManager(), "time_dialog");
            return;
        }

        super.onDisplayPreferenceDialog(preference);
    }
}
