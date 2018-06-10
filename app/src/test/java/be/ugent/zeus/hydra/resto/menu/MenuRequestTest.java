package be.ugent.zeus.hydra.resto.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class MenuRequestTest extends AbstractJsonRequestTest<List<RestoMenu>> {

    // This is the default one.
    private static final String[] RESTAURANTS = {
            "nl-debrug", // This is the default one.
            "nl",
            "en",
            "nl-heymans",
            "nl-sintjansvest",
            "nl-kantienberg"
    };

    @Override
    protected String getRelativePath() {
        return "resto/menu_default.json";
    }

    @Override
    protected JsonOkHttpRequest<List<RestoMenu>> getRequest() {
        return new MenuRequest(RuntimeEnvironment.application);
    }

    @Test
    public void testDefaultUrl() {
        MenuRequest request = new MenuRequest(RuntimeEnvironment.application);
        Context context = RuntimeEnvironment.application;
        String defaultResto = RestoPreferenceFragment.getDefaultResto(context);
        String expected = String.format(MenuRequest.OVERVIEW_URL, defaultResto);
        Assert.assertEquals(expected, request.getAPIUrl());
    }

    @Test
    public void testOtherUrl() {
        // Get random other resto, not the default one.
        final int random_resto = ThreadLocalRandom.current().nextInt(1, RESTAURANTS.length);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO_KEY, RESTAURANTS[random_resto])
                .apply();

        MenuRequest request = new MenuRequest(RuntimeEnvironment.application);
        Assert.assertEquals(String.format(MenuRequest.OVERVIEW_URL, RESTAURANTS[random_resto]), request.getAPIUrl());
    }
}