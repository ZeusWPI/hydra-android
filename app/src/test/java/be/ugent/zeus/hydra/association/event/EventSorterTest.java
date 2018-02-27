package be.ugent.zeus.hydra.association.event;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
        Gson gson = new Gson();
        InputStream eventStream = new ClassPathResource("all_activities.json").getInputStream();
        Event[] events = gson.fromJson(new InputStreamReader(eventStream), Event[].class);
        data = Arrays.asList(events);
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