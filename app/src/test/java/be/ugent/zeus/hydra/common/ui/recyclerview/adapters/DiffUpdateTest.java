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

package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Niko Strijbol
 */
@RunWith(MockitoJUnitRunner.class)
public class DiffUpdateTest {

    @Mock
    private ListUpdateCallback callback;

    @Test
    public void testNewDataNull() {
        List<Integer> expectedData = Utils.generate(Integer.class, 20).collect(Collectors.toList());

        DiffUpdate<Integer> update = new DiffUpdate<>(expectedData);
        List<Integer> actualNewData = update.newData(null);
        assertEquals(expectedData, actualNewData);
        update.applyUpdatesTo(callback);

        ArgumentCaptor<Integer> startCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(callback, times(1)).onInserted(startCaptor.capture(), sizeCaptor.capture());

        assertEquals(0, startCaptor.getValue().intValue());
        assertEquals(expectedData.size(), sizeCaptor.getValue().intValue());
    }

    @Test
    public void testNewDataEmpty() {
        List<Integer> expectedData = Utils.generate(Integer.class, 20).collect(Collectors.toList());

        DiffUpdate<Integer> update = new DiffUpdate<>(expectedData);
        List<Integer> actualNewData = update.newData(Collections.emptyList());
        assertEquals(expectedData, actualNewData);
        update.applyUpdatesTo(callback);

        ArgumentCaptor<Integer> startCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(callback, times(1)).onInserted(startCaptor.capture(), sizeCaptor.capture());

        assertEquals(0, startCaptor.getValue().intValue());
        assertEquals(expectedData.size(), sizeCaptor.getValue().intValue());
    }

    @Test
    public void testDeletionNull() {
        List<Integer> existingData = Utils.generate(Integer.class, 20).collect(Collectors.toList());

        DiffUpdate<Integer> update = new DiffUpdate<>(null);
        List<Integer> actualNewData = update.newData(existingData);
        assertNull(actualNewData);
        update.applyUpdatesTo(callback);

        ArgumentCaptor<Integer> startCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(callback, times(1)).onRemoved(startCaptor.capture(), sizeCaptor.capture());

        assertEquals(0, startCaptor.getValue().intValue());
        assertEquals(existingData.size(), sizeCaptor.getValue().intValue());
    }

    @Test
    public void testDeletionEmpty() {
        List<Integer> existingData = Utils.generate(Integer.class, 20).collect(Collectors.toList());

        DiffUpdate<Integer> update = new DiffUpdate<>(Collections.emptyList());
        List<Integer> actualNewData = update.newData(existingData);
        assertEquals(Collections.emptyList(), actualNewData);
        update.applyUpdatesTo(callback);

        ArgumentCaptor<Integer> startCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(callback, times(1)).onRemoved(startCaptor.capture(), sizeCaptor.capture());

        assertEquals(0, startCaptor.getValue().intValue());
        assertEquals(existingData.size(), sizeCaptor.getValue().intValue());
    }

    @Test
    public void testBothEmpty() {
        List<Integer> existingData = Collections.emptyList();
        List<Integer> newData = Collections.emptyList();

        DiffUpdate<Integer> update = new DiffUpdate<>(newData);
        List<Integer> actualNewData = update.newData(existingData);
        assertEquals(newData, actualNewData);
        update.applyUpdatesTo(callback);

        verifyNoInteractions(callback);
    }

    @Test
    public void testBothNull() {
        DiffUpdate<Integer> update = new DiffUpdate<>(null);
        List<Integer> actualNewData = update.newData(null);
        assertNull(actualNewData);
        update.applyUpdatesTo(callback);
        verifyNoInteractions(callback);
    }

    @Test
    public void testWithData() {
        List<Integer> existingData = Arrays.asList(1, 2, 3, 4);
        List<Integer> newData = Arrays.asList(1, 3, 4, 5);

        DiffUpdate<Integer> update = new DiffUpdate<>(newData);
        List<Integer> actualNewData = update.newData(existingData);
        assertEquals(newData, actualNewData);
        update.applyUpdatesTo(callback);

        ArgumentCaptor<Integer> startCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        // We don't verify arguments as we trust the DiffUtil classes.
        verify(callback, times(1)).onInserted(startCaptor.capture(), sizeCaptor.capture());
        verify(callback, times(1)).onRemoved(startCaptor.capture(), sizeCaptor.capture());

        verifyNoMoreInteractions(callback);
    }
}