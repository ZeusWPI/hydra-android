package be.ugent.zeus.hydra.utils;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.ExtendedSparseArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test the iterator on the sparse array.
 *
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class ExtendedSparseArrayTest {

    private ExtendedSparseArray<Integer> smallArray;

    @Before
    public void setUp() {
        smallArray = new ExtendedSparseArray<>();
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
        Iterator<Object> iterator = new ExtendedSparseArray<>().iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testKeysNormal() {
        List<Integer> result = smallArray.getKeys();
        List<Integer> expected = Arrays.asList(0, 1, 2);
        assertEquals(expected, result);
    }

    @Test
    public void testKeysEmpty() {
        List<Integer> result = new ExtendedSparseArray<Integer>().getKeys();
        assertTrue(result.isEmpty());
    }
}