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

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.preference.Preference;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.PreferenceFragment;
import be.ugent.zeus.hydra.common.ui.WebViewActivity;

/**
 * @author Niko Strijbol
 */
public class AboutFragment extends PreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_about, rootKey);

        Preference version = requirePreference("pref_about_version");

        version.setSummary(String.format(
                getString(R.string.pref_about_version_summary),
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                BuildConfig.DEBUG ? "debug" : "release"
        ));

        requirePreference("pref_about_licenses").setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(requireActivity(), WebViewActivity.class);
            intent.putExtra(WebViewActivity.TITLE, getString(R.string.pref_licenses_title));
            intent.putExtra(WebViewActivity.URL, "file:///android_res/raw/third_party_licenses.html");
            startActivity(intent);
            return false;
        });

        requirePreference("pref_about_creator_zeus")
                .setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.logo_zeus));
        requirePreference("pref_about_creator_dsa")
                .setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.logo_ugent));
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(requireContext())
                .setCurrentScreen(requireActivity(), "Settings > About", getClass().getSimpleName());
    }
}

