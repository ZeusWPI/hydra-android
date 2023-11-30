/*
 * Copyright (c) 2021 The Hydra authors
 * Copyright (c) 2022 Niko Strijbol
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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.EventObserver;
import be.ugent.zeus.hydra.common.ui.PreferenceFragment;
import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;

/**
 * Settings about the home feed.
 *
 * @author Niko Strijbol
 */
public class HomeFragment extends PreferenceFragment {

    public static final String PREF_DATA_SAVER = "pref_home_feed_save_data";
    public static final boolean PREF_DATA_SAVER_DEFAULT = false;
    private static final String PREF_RESTO_KINDS = "pref_feed_resto_kinds_v2";
    @MenuTable.DisplayKind
    private static final int PREF_RESTO_KINDS_DEFAULT = MenuTable.DisplayKind.HOT | MenuTable.DisplayKind.SOUP;
    private DeleteViewModel viewModel;

    @MenuTable.DisplayKind
    public static int feedRestoKind(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(PREF_RESTO_KINDS, PREF_RESTO_KINDS_DEFAULT);
    }
    
    public static void addFlag(Context context, @MenuTable.DisplayKind int kind) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int existing = pref.getInt(PREF_RESTO_KINDS, PREF_RESTO_KINDS_DEFAULT);
        pref.edit()
                .putInt(PREF_RESTO_KINDS, existing | kind)
                .apply();
    }

    public static void removeFlag(Context context, @MenuTable.DisplayKind int kind) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int existing = pref.getInt(PREF_RESTO_KINDS, PREF_RESTO_KINDS_DEFAULT);
        pref.edit()
                .putInt(PREF_RESTO_KINDS, existing & ~kind)
                .apply();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_home_feed, rootKey);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DeleteViewModel.class);
        viewModel.liveData().observe(getViewLifecycleOwner(), new EventObserver<>() {
            @Override
            protected void onUnhandled(Context data) {
                Toast.makeText(data, R.string.feed_pref_hidden_cleared, Toast.LENGTH_SHORT).show();
            }
        });

        requirePreference("pref_home_feed_clickable").setOnPreferenceClickListener(preference -> {
            viewModel.deleteAll();
            return true;
        });
        return v;
    }
}
