package be.ugent.zeus.hydra.preferences;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Manager;
import be.ugent.zeus.hydra.common.reporting.Reporting;

/**
 * Show preferences related to the theme of the app.
 *
 * @author Niko Strijbol
 */
public class ReportingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_reporting, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(getActivity())
                .setCurrentScreen(requireActivity(), "Settings > Reporting", getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        Manager.syncPermissions(getActivity());
    }
}