package be.ugent.zeus.hydra.fragments.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.WebViewActivity;

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
            String title = getResources().getString(R.string.title_license);
            intent.putExtra(WebViewActivity.TITLE, title);
            intent.putExtra(WebViewActivity.URL, "file:///android_asset/licenses.html");

            getActivity().startActivity(intent);

            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication app = (HydraApplication) getActivity().getApplication();
        app.sendScreenName("Setting > About");
    }
}

