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
import android.widget.Toast;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.preference.Preference;

import java.util.concurrent.atomic.AtomicInteger;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.PreferenceFragment;
import be.ugent.zeus.hydra.common.ui.WebViewActivity;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.wpi.EnableManager;

/**
 * @author Niko Strijbol
 */
public class AboutFragment extends PreferenceFragment {

    private static final int ZEUS_TIMES = 2;

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

        // Ugly one-element array.
        final AtomicInteger counter = new AtomicInteger();

        // If Zeus-mode is enabled, link to the site.
        // Otherwise, allow enabling it.
        requirePreference("pref_about_creator_zeus").setOnPreferenceClickListener(preference -> {
            if (EnableManager.isZeusModeEnabled(requireContext())) {
                Toast.makeText(requireContext(), R.string.wpi_mode_enabled, Toast.LENGTH_SHORT).show();
                NetworkUtils.maybeLaunchBrowser(requireContext(), "https://zeus.ugent.be");
            } else {
                int newValue = counter.incrementAndGet();
                int remaining = ZEUS_TIMES - newValue;
                if (remaining == 0) {
                    EnableManager.setZeusModeEnabled(requireContext(), true);
                    Toast.makeText(requireContext(), R.string.wpi_mode_enabled, Toast.LENGTH_SHORT).show();
                } else {
                    String message = requireContext().getResources().getQuantityString(R.plurals.wpi_mode_press, remaining, remaining);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
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

