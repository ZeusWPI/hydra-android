package be.ugent.zeus.hydra.resto.network;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.network.ArrayJsonSpringRequestTest;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MenuRequestTest extends ArrayJsonSpringRequestTest<RestoMenu> {

    private static final String[] restos = new String[] {
            "nl-debrug",
            "nl",
            "en",
            "nl-heymans",
            "nl-sintjansvest",
            "nl-kantienberg"
    };

    public MenuRequestTest() {
        super(RestoMenu[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("resto/menu_default.json");
    }

    @Override
    protected JsonSpringRequest<RestoMenu[]> getRequest() {
        return new MenuRequest(RuntimeEnvironment.application);
    }

    @Test
    public void testDefaultUrl() {
        MenuRequest request = new MenuRequest(RuntimeEnvironment.application);
        String expected = String.format(MenuRequest.OVERVIEW_URL, RestoPreferenceFragment.PREF_DEFAULT_RESTO);
        Assert.assertEquals(expected, request.getAPIUrl());
    }

    @Test
    public void testOtherUrl() {
        // Get random other resto, not the default one.
        final int RANDOM_RESTO = ThreadLocalRandom.current().nextInt(1, restos.length);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO_KEY, restos[RANDOM_RESTO])
                .apply();

        MenuRequest request = new MenuRequest(RuntimeEnvironment.application);
        Assert.assertEquals(String.format(MenuRequest.OVERVIEW_URL, restos[RANDOM_RESTO]), request.getAPIUrl());
    }
}