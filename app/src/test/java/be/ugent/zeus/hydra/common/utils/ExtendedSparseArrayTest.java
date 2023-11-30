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

package be.ugent.zeus.hydra.common.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.ExtendedSparseArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        List<Integer> result = smallArray.keys();
        List<Integer> expected = Arrays.asList(0, 1, 2);
        assertEquals(expected, result);
    }

    @Test
    public void testKeysEmpty() {
        List<Integer> result = new ExtendedSparseArray<Integer>().keys();
        assertTrue(result.isEmpty());
    }
}
