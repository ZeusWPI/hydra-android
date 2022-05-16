/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
    @Nullable
    private PreferenceEntry entry;

    public static void start(@NonNull Context context, @Nullable PreferenceEntry entry) {
        Intent intent = startIntent(context, entry);
        context.startActivity(intent);
    }

    public static Intent startIntent(@NonNull Context context, @Nullable PreferenceEntry entry) {
        Intent intent = new Intent(context, PreferenceActivity.class);
        if (entry != null) {
            intent.putExtra(ARG_FRAGMENT, (Parcelable) entry);
        }
        return intent;
    }


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
