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
import androidx.preference.PreferenceManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;

/**
 * @author Niko Strijbol
 */
public class SelectedResto {

    private final Context context;

    private int selectedIndex = -1;
    private List<RestoChoice> choices;
    private RestoChoice selected;

    public SelectedResto(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setData(List<RestoChoice> receivedRestos) {
        choices = receivedRestos;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedKey;
        if (selected == null) {
            selectedKey = RestoPreferenceFragment.getRestoEndpoint(context, preferences);
        } else {
            selectedKey = selected.getEndpoint();
        }
        String defaultName = context.getString(R.string.resto_default_name);
        String selectedName;
        if (selected == null) {
            selectedName = preferences.getString(RestoPreferenceFragment.PREF_RESTO_NAME, defaultName);
        } else {
            selectedName = selected.getName();
        }
        if (selected == null) {
            selected = new RestoChoice(defaultName, selectedKey);
        }
        RestoChoice selectedChoice = new RestoChoice(selectedName, selectedKey);
        selectedIndex = receivedRestos.indexOf(selectedChoice);
        if (selectedIndex == -1) {
            // The key does not exist.
            String defaultRestoEndpoint = RestoPreferenceFragment.getDefaultResto(context);
            RestoChoice defaultChoice = new RestoChoice(defaultRestoEndpoint, defaultName);
            selectedIndex = receivedRestos.indexOf(defaultChoice);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public RestoChoice getSelected() {
        return selected;
    }

    public void setSelected(RestoChoice selected) {
        this.selected = selected;
    }

    public List<Wrapper> getAsWrappers() {
        return choices.stream().map(Wrapper::new).collect(Collectors.toList());
    }

    public static class Wrapper {

        public final RestoChoice resto;
        public final String string;

        private Wrapper(RestoChoice resto) {
            this.resto = resto;
            this.string = null;
        }

        public Wrapper(String string) {
            this.resto = null;
            this.string = string;
        }

        @Override
        public String toString() {
            return resto == null ? string : resto.getName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Wrapper that = (Wrapper) o;
            return Objects.equals(resto, that.resto) &&
                    Objects.equals(string, that.string);
        }

        @Override
        public int hashCode() {
            return Objects.hash(resto, string);
        }
    }
}
