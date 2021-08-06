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

package be.ugent.zeus.hydra.preferences;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * Display an overview of the settings.
 *
 * @author Niko Strijbol
 */
public class OverviewFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        for (PreferenceEntry entry : PreferenceEntry.values()) {

            // Skip data reporting if there is no data reporting.
            if (entry == PreferenceEntry.DATA && !Reporting.hasReportingOptions()) {
                continue;
            }

            Preference preference = new Preference(context);
            preference.setTitle(entry.getName());
            preference.setSummary(entry.getDescription());
            preference.setPersistent(false);
            if (entry.getIcon() != 0) {
                int textColour = ColourUtils.resolveColour(context, R.attr.colorOnSurface);
                Drawable drawable = ViewUtils.getTintedVectorDrawableInt(context, entry.getIcon(), textColour);
                preference.setIcon(drawable);
            }
            preference.setOnPreferenceClickListener(p -> {
                PreferenceActivity.start(requireContext(), entry);
                return true;
            });
            screen.addPreference(preference);

        }
        setPreferenceScreen(screen);
    }
}
