package be.ugent.zeus.hydra.common.preferences;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.appcompat.content.res.AppCompatResources;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.WebViewActivity;

/**
 * @author Niko Strijbol
 */
public class AboutFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);

        Preference version = findPreference("pref_about_version");

        version.setSummary(String.format(
                getString(R.string.pref_about_version_summary),
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                BuildConfig.DEBUG ? "debug" : "release"
        ));

        findPreference("pref_about_licenses").setOnPreferenceClickListener(preference -> {

            Intent intent = new Intent(getActivity().getApplicationContext(), WebViewActivity.class);
            String title = getResources().getString(R.string.pref_licenses_title);
            intent.putExtra(WebViewActivity.TITLE, title);
            intent.putExtra(WebViewActivity.URL, "file:///android_asset/open_source_licenses.html");

            getActivity().startActivity(intent);

            return false;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findPreference("pref_about_creator_zeus").setIcon(AppCompatResources.getDrawable(getActivity(), R.drawable.logo_zeus));
            findPreference("pref_about_creator_dsa").setIcon(AppCompatResources.getDrawable(getActivity(), R.drawable.logo_ugent));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(getActivity())
                .setCurrentScreen(getActivity(), "Settings > About", getClass().getSimpleName());
    }
}

