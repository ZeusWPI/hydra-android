package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Niko Strijbol
 */
@RunWith(AndroidJUnit4.class)
public class PreferencesUtilsTest {

    private Context instrumentationCtx;

    @Before
    public void setup() {
        instrumentationCtx = InstrumentationRegistry.getContext();
    }

    @Test
    public void testAdd() {
        PreferencesUtils.addToStringSet(instrumentationCtx, "Test", "Value");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instrumentationCtx);
        Set<String> result = preferences.getStringSet("Test", new HashSet<>());

        assertTrue(result.contains("Value"));
    }

    @Test
    public void testRemove() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instrumentationCtx);
        Set<String> original = new HashSet<>();
        original.add("Value");
        preferences.edit().putStringSet("Test", original).apply();



        PreferencesUtils.removeFromStringSet(instrumentationCtx, "Test", "Value");
        Set<String> result = preferences.getStringSet("Test", new HashSet<>());
        assertTrue(result.isEmpty());
        PreferencesUtils.removeFromStringSet(instrumentationCtx, "Test", "Test2");
        Set<String> result2 = preferences.getStringSet("Test", new HashSet<>());
        assertTrue(result2.isEmpty());
    }
}
