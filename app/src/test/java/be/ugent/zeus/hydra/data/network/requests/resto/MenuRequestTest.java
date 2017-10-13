package be.ugent.zeus.hydra.data.network.requests.resto;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.ArrayJsonSpringRequestTest;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MenuRequestTest extends ArrayJsonSpringRequestTest<RestoMenu> {

    public MenuRequestTest() {
        super(RestoMenu[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("resto_menu.json");
    }

    @Override
    protected JsonSpringRequest<RestoMenu[]> getRequest() {
        return new MenuRequest(RuntimeEnvironment.application);
    }

    @Test
    public void testDefaultUrl() {
        MenuRequest request = new MenuRequest(RuntimeEnvironment.application);
        Assert.assertEquals(MenuRequest.NORMAL_URL, request.getAPIUrl());
    }

    @Test
    public void testNormalUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO, RestoPreferenceFragment.PREF_RESTO_NORMAL)
                .apply();

        MenuRequest request = new MenuRequest(RuntimeEnvironment.application);
        Assert.assertEquals(MenuRequest.NORMAL_URL, request.getAPIUrl());
    }

    @Test
    public void testSintJanUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO, RestoPreferenceFragment.PREF_RESTO_SINT_JAN)
                .apply();

        MenuRequest request = new MenuRequest(RuntimeEnvironment.application);
        Assert.assertEquals(MenuRequest.SINT_JAN_URL, request.getAPIUrl());
    }
}