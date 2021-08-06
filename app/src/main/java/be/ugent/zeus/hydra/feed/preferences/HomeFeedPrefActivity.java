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
