package be.ugent.zeus.hydra.preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityPreferencesBinding;

/**
 * Activity that will show a fragment.
 *
 * @author Niko Strijbol
 */
public class PreferenceActivity extends BaseActivity<ActivityPreferencesBinding> {

    /**
     * Argument for the activity, indicating which fragment should be shown.
     */
    public final static String ARG_FRAGMENT = "be.ugent.zeus.hydra.preferences.id";

    private static final int settingsTitle = R.string.action_view_settings;

    public static void start(@NonNull Context context, @Nullable PreferenceEntry entry) {
        Intent intent = new Intent(context, PreferenceActivity.class);
        if (entry != null) {
            intent.putExtra(ARG_FRAGMENT, (Parcelable) entry);
        }
        context.startActivity(intent);
    }

    @Nullable
    private PreferenceEntry entry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityPreferencesBinding::inflate);

        if (savedInstanceState != null) {
            // If a specific screen is requested, use that one.
            entry = savedInstanceState.getParcelable(ARG_FRAGMENT);
        } else if (Intent.ACTION_MANAGE_NETWORK_USAGE.equals(getIntent().getAction())) {
            // We come from the device data usage settings screen, show network options.
            entry = PreferenceEntry.HOME;
        } else {
            // Nothing was requested, show the overview.
            entry = getIntent().getParcelableExtra(ARG_FRAGMENT);
        }

        setFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        entry = intent.getParcelableExtra(ARG_FRAGMENT);
        setFragment();
    }

    private void setFragment() {
        Fragment fragment;
        if (entry == null) {
            setTitle(settingsTitle);
            fragment = new OverviewFragment();
        } else {
            setTitle(entry.getName());
            fragment = entry.getFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_FRAGMENT, entry);
    }

    @Override
    public Intent getParentActivityIntent() {
        if (entry != null) {
            // We need to return to the overview.
            return new Intent(this, PreferenceActivity.class);
        }
        return super.getParentActivityIntent();
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        if (entry != null) {
            // We need to return to the overview.
            return new Intent(this, PreferenceActivity.class);
        }
        return super.getSupportParentActivityIntent();
    }
}
