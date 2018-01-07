package be.ugent.zeus.hydra.ui.preferences;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.database.RepositoryFactory;
import be.ugent.zeus.hydra.domain.repository.CardRepository;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment;

/**
 * Settings about the home feed.
 *
 * @author Niko Strijbol
 */
public class HomeFragment extends PreferenceFragment {

    public static final String PREF_DATA_SAVER = "pref_home_feed_save_data";
    public static final boolean PREF_DATA_SAVER_DEFAULT = false;
    private static final String TAG = "HomeFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_home_feed);

        findPreference("pref_home_feed_clickable")
                .setOnPreferenceClickListener(preference -> {
                    deleteAll();
                    return true;
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        HydraApplication.getApplication(getActivity()).sendScreenName("Settings > Home feed");
    }

    private void deleteAll() {
        AsyncTask.execute(() -> {
            CardRepository cardRepository = RepositoryFactory.getCardRepository(getActivity());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            cardRepository.deleteAll();
            boolean newValue = !preferences.getBoolean(HomeFeedFragment.PREF_DISABLED_CARD_HACK, true);
            preferences.edit()
                    .putBoolean(HomeFeedFragment.PREF_DISABLED_CARD_HACK, newValue)
                    .apply();
            Log.i(TAG, "All hidden cards removed.");
        });

        Toast.makeText(getActivity(), R.string.pref_home_feed_cleared, Toast.LENGTH_SHORT).show();
    }
}