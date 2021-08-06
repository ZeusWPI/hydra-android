/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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