package be.ugent.zeus.hydra.association.network;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.Event;
import be.ugent.zeus.hydra.utils.PreferencesUtils;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING;
import static junit.framework.Assert.assertTrue;

/**
 * TODO: we need more events!
 *
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
@RequiresApi(api = Build.VERSION_CODES.N)
public class EventFilterTest {

    private List<Event> data;
    private List<Association> associations;

    @Before
    public void setUp() throws IOException {
        Gson gson = new Gson();
        InputStream eventStream = new ClassPathResource("all_activities.json").getInputStream();
        Event[] events = gson.fromJson(new InputStreamReader(eventStream), Event[].class);
        data = Arrays.asList(events);
        InputStream associationStream = new ClassPathResource("associations.json").getInputStream();
        Association[] associations = gson.fromJson(new InputStreamReader(associationStream), Association[].class);
        this.associations = Arrays.asList(associations);
    }

    @Test
    public void testNoFilter() {

        // Add the associations we want to filter out. For that, we select 10 associations at random from the list.
        List<Association> copy = new ArrayList<>(associations);
        Collections.shuffle(copy);
        Set<String> toRemoveIds = copy.subList(0, 10)
                .stream()
                .map(Association::getInternalName)
                .collect(Collectors.toSet());

        // Add those to the preferences
        PreferencesUtils.addToStringSet(RuntimeEnvironment.application, PREF_ASSOCIATIONS_SHOWING, toRemoveIds);

        // Do the filtering
        EventFilter filter = new EventFilter(RuntimeEnvironment.application);
        List<Event> result = filter.apply(data);

        assertTrue(
                result.stream()
                        .noneMatch(event -> toRemoveIds.contains(event.getAssociation().getInternalName()))
        );
    }
}