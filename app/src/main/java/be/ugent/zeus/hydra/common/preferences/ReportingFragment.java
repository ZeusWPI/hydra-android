package be.ugent.zeus.hydra.common.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;

/**
 * Show preferences related to the theme of the app.
 *
 * @author Niko Strijbol
 */
public class ReportingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_reporting);
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(getActivity())
                .setCurrentScreen(getActivity(), "Settings > Reporting", getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        Reporting.syncPermissions(getActivity());
    }
}