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

package be.ugent.zeus.hydra.resto.meta.selectable;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.List;

import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;

/**
 * @author Niko Strijbol
 */
public class SelectedResto {

    private SelectedResto() {
        // No.
    }

    public static int findChoiceIndex(Context context, List<RestoChoice> choices, @Nullable RestoChoice previouslySelected) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        RestoChoice selected;
        if (previouslySelected == null) {
            var selectedKey = RestoPreferenceFragment.getRestoEndpoint(context, preferences);
            var selectedName = RestoPreferenceFragment.getRestoName(context, preferences);
            selected = new RestoChoice(selectedName, selectedKey);
        } else {
            selected = previouslySelected;
        }
        int selectedIndex = choices.indexOf(selected);
        if (selectedIndex == -1) {
            // The key does not exist.
            String defaultEndpoint = RestoPreferenceFragment.getDefaultRestoEndpoint(context);
            String defaultName = RestoPreferenceFragment.getDefaultRestoName(context);
            RestoChoice defaultChoice = new RestoChoice(defaultName, defaultEndpoint);
            selectedIndex = choices.indexOf(defaultChoice);
        }

        return selectedIndex;
    }
}
