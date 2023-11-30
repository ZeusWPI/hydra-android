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

package be.ugent.zeus.hydra.resto.menu;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.preference.PreferenceManager;

import java.time.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;

/**
 * Filters a list of menu's to only retain the useful ones.
 *
 * @author Niko Strijbol
 */
public class MenuFilter implements Function<List<RestoMenu>, List<RestoMenu>> {

    private final SharedPreferences preferences;
    private final Clock clock;

    public MenuFilter(Context context) {
        this(context, Clock.systemDefaultZone());
    }

    @VisibleForTesting
    MenuFilter(Context context, Clock clock) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.clock = clock;
    }

    @VisibleForTesting
    MenuFilter(SharedPreferences preferences, Clock clock) {
        this.preferences = preferences;
        this.clock = clock;
    }

    /**
     * Filter the menus. Note that the current time can be set using {@link #clock} (useful for testing).
     * The filter will act as follows:
     *
     * <ul>
     *     <li>If the menu's date is before today, the menu will be removed. (menu.date < now)</li>
     *     <li>If the menu's date is after today, the menu will be retained. (menu.date > now)</li>
     *     <li>If the menu's date is the same as today's date, the filtering depends on the user option
     *     {@link RestoPreferenceFragment#PREF_RESTO_CLOSING_HOUR}. The default value is {@link RestoPreferenceFragment#DEFAULT_CLOSING_TIME}.
     *     <ul>
     *         <li>If the current time is before the time in the settings, the menu will be retained.</li>
     *         <li>If the current time is equal to or after the time set in the settings, the menu will be removed.</li>
     *     </ul>
     *     </li>
     * </ul>
     *
     * @param restoMenus The menus to filter. Cannot be null.
     * @return The filtered menus. Will not be null.
     */
    @NonNull
    @Override
    public List<RestoMenu> apply(@NonNull List<RestoMenu> restoMenus) {
        LocalTime closingHour = LocalTime.parse(
                preferences.getString(
                        RestoPreferenceFragment.PREF_RESTO_CLOSING_HOUR,
                        RestoPreferenceFragment.DEFAULT_CLOSING_TIME
                )
        );
        LocalDate today = LocalDate.now(clock);
        boolean isEarlyEnough = LocalDateTime.now(clock).isBefore(LocalDateTime.of(today, closingHour));

        return restoMenus.stream()
                .filter(m -> m.date().isAfter(today) || (m.date().isEqual(today) && isEarlyEnough))
                .collect(Collectors.toList());
    }
}
