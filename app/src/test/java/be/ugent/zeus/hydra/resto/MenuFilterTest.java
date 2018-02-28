package be.ugent.zeus.hydra.resto;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.resto.MenuFilter;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.threeten.bp.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class MenuFilterTest {

    private final Instant cutOff = Instant.parse("2007-12-03T10:15:00.000Z");
    private final Clock clock = Clock.fixed(cutOff, ZoneOffset.UTC);

    @Test
    public void testDefaults() throws IOException {
        RestoMenu[] menus = Utils.readJsonResource(RestoMenu[].class, "resto/menu_default.json");
        // Set half of the restos to before, half after.
        for (int i = 0; i < menus.length; i++) {
            if (i < menus.length / 2) {
                menus[i].setDate(LocalDateTime.ofInstant(cutOff, ZoneId.systemDefault()).toLocalDate().minusDays(menus.length / 2 - i));
            } else {
                menus[i].setDate(LocalDateTime.ofInstant(cutOff, ZoneId.systemDefault()).toLocalDate().plusDays(i - menus.length / 2));
            }
        }

        List<RestoMenu> restoMenus = Arrays.asList(menus);
        MenuFilter filter = new MenuFilter(RuntimeEnvironment.application, clock);
        List<RestoMenu> result = filter.apply(restoMenus);
        assertThat(result, hasSize(restoMenus.size() / 2));
        assertTrue(result.stream().noneMatch(restoMenu -> restoMenu.getDate().isBefore(cutOff.atZone(ZoneId.systemDefault()).toLocalDate())));
    }

    @Test
    public void testSameDayBefore() {

        RestoMenu restoMenu = generate(RestoMenu.class);
        restoMenu.setDate(LocalDateTime.ofInstant(cutOff, ZoneId.systemDefault()).toLocalDate());

        // If the time in the settings is after the current time, the resto must be allowed to pass.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO_CLOSING_HOUR, "11:00")
                .apply();

        MenuFilter filter = new MenuFilter(RuntimeEnvironment.application, clock);
        List<RestoMenu> result = filter.apply(Collections.singletonList(restoMenu));

        assertThat(result, contains(restoMenu));
    }

    @Test
    public void testSameDayAfter() {
        RestoMenu restoMenu = generate(RestoMenu.class);
        restoMenu.setDate(LocalDateTime.ofInstant(cutOff, ZoneId.systemDefault()).toLocalDate());

        // If the time in the settings is after the current time, the resto must be allowed to pass.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO_CLOSING_HOUR, "09:00")
                .apply();

        MenuFilter filter = new MenuFilter(RuntimeEnvironment.application, clock);
        List<RestoMenu> result = filter.apply(Collections.singletonList(restoMenu));

        assertThat(result, empty());
    }

    @Test
    public void testSameDayOnMoment() {
        RestoMenu restoMenu = generate(RestoMenu.class);
        restoMenu.setDate(LocalDateTime.ofInstant(cutOff, ZoneId.systemDefault()).toLocalDate());

        // If the time in the settings is after the current time, the resto must be allowed to pass.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putString(RestoPreferenceFragment.PREF_RESTO_CLOSING_HOUR, "10:15")
                .apply();

        // TODO: for some weird reason, if we do not pass the same preference, it doesn't work, even if the debugger
        // says they are the same instance and all that. Perhaps this is a bug in Robolectric?
        MenuFilter filter = new MenuFilter(preferences, clock);
        List<RestoMenu> result = filter.apply(Collections.singletonList(restoMenu));

        assertThat(result, empty());
    }
}