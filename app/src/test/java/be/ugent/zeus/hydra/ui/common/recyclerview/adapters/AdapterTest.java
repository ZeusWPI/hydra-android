package be.ugent.zeus.hydra.ui.common.recyclerview.adapters;

import be.ugent.zeus.hydra.BuildConfig;
import java8.util.stream.Collectors;
import java8.util.stream.IntStreams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AdapterTest {

    private static final int NR_OF_OBJECTS = 100;

    private TestItemAdapter adapter;

    @Before
    public void setUp() {
        adapter = new TestItemAdapter();
    }

    // Add some objects
    private void fillAdapter() {
        List<Integer> integers = IntStreams.range(0, NR_OF_OBJECTS)
                .mapToObj(i -> i)
                .collect(Collectors.toList());
        adapter.setItems(integers);
    }

    @Test
    public void testEmpty() {
        assertEquals(0, adapter.getItemCount());
        assertTrue(adapter.getItems().isEmpty());
    }

    @Test
    public void testSetItemsAndFull() {
        fillAdapter();
        assertTrue(adapter.takeChanged());
        assertEquals(NR_OF_OBJECTS, adapter.getItemCount());
        assertEquals(Adapter.ITEM_TYPE, adapter.getItemViewType(0));
    }

    @Test
    public void testClear() {
        fillAdapter();
        adapter.clear();
        testEmpty();
    }
}