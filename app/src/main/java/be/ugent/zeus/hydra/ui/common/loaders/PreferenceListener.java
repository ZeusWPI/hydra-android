package be.ugent.zeus.hydra.ui.common.loaders;

import android.content.SharedPreferences;
import android.support.v4.content.Loader;
import java8.util.function.Predicate;

/**
 * Listens to changes in preferences.
 *
 * @author Niko Strijbol
 */
@Deprecated
public class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final Loader<?> loader;
    private final Predicate<String> condition;

    public PreferenceListener(Loader<?> loader, Predicate<String> condition) {
        this.loader = loader;
        this.condition = condition;
    }

    public PreferenceListener(Loader<?> loader, String key) {
        this.loader = loader;
        this.condition = s -> s.equals(key);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (condition.test(key)) {
            loader.onContentChanged();
        }
    }
}