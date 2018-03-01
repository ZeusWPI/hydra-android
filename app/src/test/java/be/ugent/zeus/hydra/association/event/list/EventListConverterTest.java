package be.ugent.zeus.hydra.association.event.list;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.association.event.Event;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class EventListConverterTest {

    private List<Event> data;

    @Before
    public void setUp() throws IOException {
        Gson gson = new Gson();
        InputStream eventStream = new ClassPathResource("all_activities.json").getInputStream();
        Event[] events = gson.fromJson(new InputStreamReader(eventStream), Event[].class);
        data = Arrays.asList(events);
    }

    @Test
    public void testConversion() {

        List<EventItem> result = new EventListConverter().apply(data);

        assertTrue(result.size() > data.size());

        // Set of items will start with a header, and end with an item marked as last.
        boolean previousWasHeader = false;
        boolean previousWasLastOfSection = true;
        boolean previousWasFirstOfSection = false;

        // Check the first event manually.
        for (EventItem item: result) {
            if (previousWasLastOfSection) {
                assertTrue(item.isHeader());
                previousWasHeader = true;
                previousWasLastOfSection = false;
            } else if (previousWasHeader) {
                assertTrue(item.isFirstOfSection());
                previousWasHeader = false;
                previousWasFirstOfSection = true;
            } else if (previousWasFirstOfSection) {
                previousWasFirstOfSection = false;
            } else if (item.isItem()) {
                previousWasLastOfSection = item.isLastOfSection();
            }
        }

        // Manually assert the last element is the last in the section.
        assertTrue(result.get(result.size() - 1).isLastOfSection());

        // Assert the events are sorted by date. Extract all dates.
        List<LocalDate> dates = result.stream()
                .filter(EventItem::isHeader)
                .map(EventItem::getHeader)
                .collect(Collectors.toList());

        List<LocalDate> sorted = new ArrayList<>(dates);
        Collections.sort(sorted);

        // Check for sorting.
        assertEquals(sorted, dates);
    }

    @Test
    public void testEmpty() {
        EventListConverter converter = new EventListConverter();
        List<Event> empty = Collections.emptyList();
        List<EventItem> result = converter.apply(empty);
        assertTrue(result.isEmpty());
    }
}