package be.ugent.zeus.hydra.preferences;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;

/**
 * Show preferences related to the news-section, specifically for the use of Custom Tabs.
 *
 * @author Niko Strijbol
 */
public class ArticleFragment extends PreferenceFragmentCompat {

    public static final String PREF_USE_CUSTOM_TABS = "pref_article_use_custom_tabs";
    public static final boolean PREF_USE_CUSTOM_TABS_DEFAULT = true;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_article, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(requireContext())
                .setCurrentScreen(requireActivity(), "Settings > Custom Tabs", getClass().getSimpleName());
    }
}