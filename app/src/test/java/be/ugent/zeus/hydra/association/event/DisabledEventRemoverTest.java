package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;

import androidx.test.core.app.ApplicationProvider;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.Utils;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static be.ugent.zeus.hydra.association.preference.AssociationSelectionPreferenceFragment.PREF_ASSOCIATIONS_SHOWING;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
@RequiresApi(api = Build.VERSION_CODES.N)
public class DisabledEventRemoverTest {

    private List<Event> data;
    private List<Association> associations;

    @Before
    public void setUp() throws IOException {
        Moshi moshi = InstanceProvider.getMoshi();
        data = Utils.readJson(moshi, "all_activities.json",
                Types.newParameterizedType(List.class, Event.class));
        this.associations = Utils.readJson(moshi, "associations.json",
                Types.newParameterizedType(List.class, Association.class));
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

        Context context = ApplicationProvider.getApplicationContext();

        // Add those to the preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> newSet = new HashSet<>(preferences.getStringSet(PREF_ASSOCIATIONS_SHOWING, Collections.emptySet()));
        newSet.addAll(toRemoveIds);
        preferences.edit().putStringSet(PREF_ASSOCIATIONS_SHOWING, newSet).apply();

        // Do the filtering
        DisabledEventRemover filter = new DisabledEventRemover(context);
        List<Event> result = filter.apply(data);

        assertTrue(
                "Events that should have been filtered away are still present.",
                result.stream().noneMatch(event -> toRemoveIds.contains(event.getAssociation().getInternalName()))
        );
    }
}
