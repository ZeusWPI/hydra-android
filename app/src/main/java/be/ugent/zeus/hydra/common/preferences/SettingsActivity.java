package be.ugent.zeus.hydra.common.preferences;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;

import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.preferences.ArticleFragment;
import be.ugent.zeus.hydra.common.ui.AppCompatPreferenceActivity;
import be.ugent.zeus.hydra.feed.preferences.HomeFragment;
import be.ugent.zeus.hydra.preferences.AboutFragment;
import be.ugent.zeus.hydra.preferences.ReportingFragment;
import be.ugent.zeus.hydra.preferences.ThemeFragment;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;

/**
 * Display settings.
 *
 * @author Niko Strijbol
 * @author Rien Maertens
 */
@Deprecated
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications. Make sure to deny any unknown fragments here.
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || RestoPreferenceFragment.class.getName().equals(fragmentName)
                || HomeFragment.class.getName().equals(fragmentName)
                || AboutFragment.class.getName().equals(fragmentName)
                || ArticleFragment.class.getName().equals(fragmentName)
                || ThemeFragment.class.getName().equals(fragmentName)
                || ReportingFragment.class.getName().equals(fragmentName);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //This recreates the main activity instead of resuming. Why?
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onIsMultiPane() {
        return this.getResources().getConfiguration().isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE);
    }
}
