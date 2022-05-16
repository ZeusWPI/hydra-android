/*
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

package be.ugent.zeus.hydra.wpi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.preference.PreferenceManager;

import java.util.Collections;

import be.ugent.zeus.hydra.R;

/**
 * Utilities to check if Zeus-mode has been enabled or not.
 *
 * @author Niko Strijbol
 */
public class EnableManager {
    private static final String PREF_ENABLE_ZEUS_MODE = "pref_enable_zeus_mode";
    private static final String ZEUS_SHORTCUT_ID = "be.ugent.zeus.hydra.shortcut.wpi";

    private EnableManager() {
        // No.
    }

    public static boolean isZeusModeEnabled(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREF_ENABLE_ZEUS_MODE, false);
    }

    public static void setZeusModeEnabled(Context context, boolean enabled) {
        if (enabled) {
            ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(context, ZEUS_SHORTCUT_ID)
                    .setShortLabel("Zeus WPI")
                    .setLongLabel(context.getString(R.string.drawer_title_zeus))
                    .setIcon(IconCompat.createWithResource(context, R.drawable.logo_tap))
                    .setIntent(new Intent(Intent.ACTION_VIEW, null, context, WpiActivity.class))
                    .build();
            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut);
        } else {
            ShortcutManagerCompat.removeDynamicShortcuts(context, Collections.singletonList(ZEUS_SHORTCUT_ID));
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putBoolean(PREF_ENABLE_ZEUS_MODE, enabled)
                .apply();
    }
}
