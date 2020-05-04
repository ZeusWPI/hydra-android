package be.ugent.zeus.hydra.resto.meta.selectable;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.List;

import java9.util.Objects;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

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

    public void setSelected(RestoChoice selected) {
        this.selected = selected;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public RestoChoice getSelected() {
        return selected;
    }

    public List<Wrapper> getAsWrappers() {
        return StreamSupport.stream(choices).map(Wrapper::new).collect(Collectors.toList());
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
