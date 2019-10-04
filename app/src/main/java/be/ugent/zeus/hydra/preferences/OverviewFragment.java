package be.ugent.zeus.hydra.preferences;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import be.ugent.zeus.hydra.common.ui.ViewUtils;

/**
 * Display an overview of the settings.
 *
 * @author Niko Strijbol
 */
public class OverviewFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        for (PreferenceEntry entry : PreferenceEntry.values()) {
            Preference preference = new Preference(context);
            preference.setTitle(entry.getName());
            preference.setSummary(entry.getDescription());
            preference.setPersistent(false);
            if (entry.getIcon() != 0) {
                int textColour = ViewUtils.getColor(context, android.R.attr.textColor);
                Drawable drawable = ViewUtils.getTintedVectorDrawableInt(context, entry.getIcon(), textColour);
                preference.setIcon(drawable);
            }
            preference.setOnPreferenceClickListener(p -> {
                PreferenceActivity.start(requireContext(), entry);
                return true;
            });
            screen.addPreference(preference);
        }
        setPreferenceScreen(screen);
    }
}
