package be.ugent.zeus.hydra.utils;

import be.ugent.zeus.hydra.BuildConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Test the iterator on the sparse array.
 *
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class IterableSparseArrayTest {

    private IterableSparseArray<Integer> smallArray;

    @Before
    public void setUp() {
        smallArray = new IterableSparseArray<>();
        smallArray.put(0, 2);
        smallArray.put(1, 3);
        smallArray.put(2, 4);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() throws UnsupportedOperationException {
        smallArray.iterator().remove();
    }

    @Test
    public void testNormal() {
        Iterator<Integer> iterator = smallArray.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(2, (int) iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(3, (int) iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(4, (int) iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testEmpty() {
        Iterator<Object> iterator = new IterableSparseArray<>().iterator();
        assertFalse(iterator.hasNext());
    }
}