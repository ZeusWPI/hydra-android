package be.ugent.zeus.hydra.preferences;

import android.content.Intent;
import android.os.Build;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requirePreference("pref_about_creator_zeus")
                    .setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.logo_zeus));
            requirePreference("pref_about_creator_dsa")
                    .setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.logo_ugent));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(requireContext())
                .setCurrentScreen(requireActivity(), "Settings > About", getClass().getSimpleName());
    }
}

