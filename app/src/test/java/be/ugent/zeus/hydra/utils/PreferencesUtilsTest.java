package be.ugent.zeus.hydra.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class PreferencesUtilsTest {

    private static final String TEST_KEY = "test_key";

    @Test
    public void addToStringSet() {
        String value = "1";
        PreferencesUtils.addToStringSet(RuntimeEnvironment.application, TEST_KEY, value);
        Set<String> result = PreferencesUtils.getStringSet(RuntimeEnvironment.application, TEST_KEY);
        assertThat(result, contains(value));
    }

    @Test
    public void addToStringSetMore() {
        String[] toAdd = {"1", "2", "3"};
        PreferencesUtils.addToStringSet(RuntimeEnvironment.application, TEST_KEY, Arrays.asList(toAdd));
        Set<String> result = PreferencesUtils.getStringSet(RuntimeEnvironment.application, TEST_KEY);
        assertThat(result, contains(toAdd));
    }

    @Test
    public void removeFromStringSet() {

        String[] data = {"1", "2", "3", "4", "a", "5"};

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putStringSet(TEST_KEY, new HashSet<>(Arrays.asList(data)))
                .apply();

        PreferencesUtils.removeFromStringSet(RuntimeEnvironment.application, TEST_KEY, "a");
        Set<String> result = PreferencesUtils.getStringSet(RuntimeEnvironment.application, TEST_KEY);
        assertThat(result, not(contains("a")));
        assertThat(result, containsInAnyOrder("1", "2", "3", "4", "5"));
    }

    @Test
    public void removeFromStringSetMore() {

        String[] data = {"1", "2", "3", "4", "a", "5"};

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putStringSet(TEST_KEY, new HashSet<>(Arrays.asList(data)))
                .apply();

        PreferencesUtils.removeFromStringSet(RuntimeEnvironment.application, TEST_KEY, Arrays.asList(data));
        Set<String> result = PreferencesUtils.getStringSet(RuntimeEnvironment.application, TEST_KEY);
        assertThat(result, empty());
    }
}