package be.ugent.zeus.hydra.feed.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityPreferencesHomefeedBinding;
import be.ugent.zeus.hydra.preferences.PreferenceActivity;
import be.ugent.zeus.hydra.preferences.PreferenceEntry;

/**
 * Allow the user to select which home feed things he/she wants.
 *
 * @author Niko Strijbol
 */
public class HomeFeedPrefActivity extends BaseActivity<ActivityPreferencesHomefeedBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityPreferencesHomefeedBinding::inflate);
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null && intent.getComponent() != null
                && intent.getComponent().getClassName().equals(PreferenceActivity.class.getName())) {
            intent.putExtra(PreferenceActivity.ARG_FRAGMENT, (Parcelable) PreferenceEntry.HOME);
        }

        return intent;
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null && intent.getComponent() != null
                && intent.getComponent().getClassName().equals(PreferenceActivity.class.getName())) {
            intent.putExtra(PreferenceActivity.ARG_FRAGMENT, (Parcelable) PreferenceEntry.HOME);
        }

        return intent;
    }
}
