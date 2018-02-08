package be.ugent.zeus.hydra.association.news;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

/**
 * Show preferences related to the news-section.
 *
 * @author Niko Strijbol
 */
public class ArticlePreferenceFragment extends PreferenceFragment {

    public static final String PREF_USE_CUSTOM_TABS = "pref_article_use_custom_tabs";
    public static final boolean PREF_USE_CUSTOM_TABS_DEFAULT = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_article);
    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > News");
    }
}