package be.ugent.zeus.hydra.association;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

import java.util.*;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.association.list.MemoryAssociationMap;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class AssociationStoreTest {

    private Context context;

    @Before
    public void setUp() {
        this.context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void readingWithoutDataReturnsNull() {
        assertNull(AssociationStore.read(context));
    }

    @Test
    public void readWithData() {
        List<Association> associations = Utils.generate(Association.class, 2).collect(Collectors.toList());
        MemoryAssociationMap map = new MemoryAssociationMap(associations);
        Set<String> result = AssociationStore.read(context, map);
        assertTrue(result.containsAll(associations.stream().map(Association::getAbbreviation).collect(Collectors.toSet())));

        // Assert that the initialisation has happened.
        result = AssociationStore.read(context);
        assertNotNull(result);
        assertTrue(result.containsAll(associations.stream().map(Association::getAbbreviation).collect(Collectors.toSet())));
    }

    @Test
    public void blacklistWithoutInitialisationDoesNothing() {
        Association association = Utils.generate(Association.class);
        AssociationStore.blacklist(context, association.getAbbreviation());
        assertNull(AssociationStore.read(context));
    }

    @Test
    public void blacklistWithInitialisationSaves() {
        Association association = Utils.generate(Association.class);
        HashSet<String> whitelist = new HashSet<>(Collections.singletonList(association.getAbbreviation()));
        AssociationStore.replace(context, whitelist);
        assertEquals(whitelist, AssociationStore.read(context));
        AssociationStore.blacklist(context, association.getAbbreviation());
        assertEquals(Collections.emptySet(), AssociationStore.read(context));
    }

    @Test
    public void whitelistWithoutInitialisationDoesNothing() {
        Association association = Utils.generate(Association.class);
        AssociationStore.whitelist(context, association.getAbbreviation());
        assertNull(AssociationStore.read(context));
    }

    @Test
    public void whitelistWithInitialisationSaves() {
        Association association = Utils.generate(Association.class);
        Set<String> whitelist = Collections.<String>emptySet();
        AssociationStore.replace(context, whitelist);
        assertEquals(whitelist, AssociationStore.read(context));
        AssociationStore.whitelist(context, association.getAbbreviation());
        assertEquals(new HashSet<>(Collections.singleton(association.getAbbreviation())), AssociationStore.read(context));
    }
}