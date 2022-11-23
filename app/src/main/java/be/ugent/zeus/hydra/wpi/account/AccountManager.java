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
package be.ugent.zeus.hydra.wpi.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;

/**
 * @author Niko Strijbol
 */
public class AccountManager {
    private static final String PREF_WPI_TAB_API_KEY = "pref_wpi_tab_api_key";
    private static final String PREF_WPI_TAP_API_KEY = "pref_wpi_tap_api_key";
    private static final String PREF_WPI_DOOR_API_KEY = "pref_wpi_door_api_key";
    private static final String PREF_WPI_USERNAME = "pref_wpi_username";

    private AccountManager() {
        // No.
    }

    public static void saveData(Context context, String tabKey, String tapKey, String username, String doorKey) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putString(PREF_WPI_TAB_API_KEY, tabKey)
                .putString(PREF_WPI_TAP_API_KEY, tapKey)
                .putString(PREF_WPI_USERNAME, username)
                .putString(PREF_WPI_DOOR_API_KEY, doorKey)
                .apply();
    }

    @Nullable
    public static String getTapKey(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_WPI_TAP_API_KEY, null);
    }

    @Nullable
    public static String getTabKey(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_WPI_TAB_API_KEY, null);
    }

    @Nullable
    public static String getUsername(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_WPI_USERNAME, context.getString(R.string.wpi_product_na));
    }

    @Nullable
    public static String getDoorKey(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_WPI_DOOR_API_KEY, null);
    }
    
    public static boolean hasUsername(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return !TextUtils.isEmpty(preferences.getString(PREF_WPI_USERNAME, null));
    }
}
