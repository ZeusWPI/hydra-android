package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Niko Strijbol
 */
@RunWith(MockitoJUnitRunner.class)
public class DumbUpdateTest {

    @Mock
    private ListUpdateCallback callback;

    @Test
    public void testNonNull() {
        List<Integer> data = generate(Integer.class, 20).collect(Collectors.toList());
        DumbUpdate<Integer> update = new DumbUpdate<>(data);
        assertEquals(data, update.getNewData(null));
        update.applyUpdatesTo(callback);
        verify(callback, times(1)).onDataSetChanged();
    }

    @Test
    public void testNull() {
        DumbUpdate<Integer> update = new DumbUpdate<>(null);
        assertNull(update.getNewData(null));
        update.applyUpdatesTo(callback);
        verify(callback, times(1)).onDataSetChanged();
    }

}