package be.ugent.zeus.hydra.association.event;

import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.Utils;
import com.squareup.moshi.Types;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class EventSorterTest {

    private List<Event> data;

    @Before
    public void setUp() throws IOException {
        data = Utils.readJson(InstanceProvider.getMoshi(), "all_activities.json",
                Types.newParameterizedType(List.class, Event.class));
    }

    @Test
    public void testSorting() {
        // Make copy to sort.
        List<Event> expected = new ArrayList<>(data);
        Collections.sort(expected);

        EventSorter sorter = new EventSorter();
        List<Event> actual = sorter.apply(data);

        assertEquals(expected, actual);
    }
}