package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.v7.util.ListUpdateCallback;
import be.ugent.zeus.hydra.testing.Utils;
import java8.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Integer> actualNewData = update.getNewData(null);
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
        List<Integer> actualNewData = update.getNewData(Collections.emptyList());
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
        List<Integer> actualNewData = update.getNewData(existingData);
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
        List<Integer> actualNewData = update.getNewData(existingData);
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
        List<Integer> actualNewData = update.getNewData(existingData);
        assertEquals(newData, actualNewData);
        update.applyUpdatesTo(callback);

        verifyZeroInteractions(callback);
    }

    @Test
    public void testBothNull() {
        DiffUpdate<Integer> update = new DiffUpdate<>(null);
        List<Integer> actualNewData = update.getNewData(null);
        assertNull(actualNewData);
        update.applyUpdatesTo(callback);
        verifyZeroInteractions(callback);
    }

    @Test
    public void testWithData() {
        List<Integer> existingData = Lists.of(1, 2, 3, 4);
        List<Integer> newData = Lists.of(1, 3, 4, 5);

        DiffUpdate<Integer> update = new DiffUpdate<>(newData);
        List<Integer> actualNewData = update.getNewData(existingData);
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