package be.ugent.zeus.hydra.resto.menu;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
        return new MenuRequest(context);
    }

    @Test
    public void testDefaultUrlIsUsed() {
        MenuRequest request = new MenuRequest(context);
        String defaultResto = RestoPreferenceFragment.getDefaultResto(context);
        String expected = String.format(MenuRequest.OVERVIEW_URL, defaultResto);
        Assert.assertEquals(expected, request.getAPIUrl());
    }

    @Test
    public void testOtherUrl() {
        // Get random other resto, not the default one.
        final int random_resto = ThreadLocalRandom.current().nextInt(1, RESTAURANTS.length);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO_KEY, RESTAURANTS[random_resto])
                .apply();

        MenuRequest request = new MenuRequest(context);
        Assert.assertEquals(String.format(MenuRequest.OVERVIEW_URL, RESTAURANTS[random_resto]), request.getAPIUrl());
    }
}
