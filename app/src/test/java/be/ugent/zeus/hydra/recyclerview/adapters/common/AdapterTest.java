package be.ugent.zeus.hydra.recyclerview.adapters.common;

import java8.util.stream.Collectors;
import java8.util.stream.IntStreams;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test of subset of {@link Adapter} behaviour.
 *
 * TODO: find solution to mock notifyDataSetChanged.
 *
 * @author Niko Strijbol
 */
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

//    @Test
//    public void testSetItemsAndFull() {
//        fillAdapter();
//        assertTrue(adapter.takeChanged());
//        assertEquals(NR_OF_OBJECTS, adapter.getItemCount());
//        assertEquals(Adapter.ITEM_TYPE, adapter.getItemViewType(0));
//    }
//
//    @Test
//    public void testClear() {
//        fillAdapter();
//        adapter.clear();
//        testEmpty();
//    }
}