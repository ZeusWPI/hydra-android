package be.ugent.zeus.hydra.association.event.list;

import android.annotation.SuppressLint;

import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.testing.Utils;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@SuppressLint("NewApi")
public class EventSearchPredicateTest {

    private List<Event> data;

    @Before
    public void setUp() throws IOException {
        Gson gson = new Gson();
        InputStream eventStream = new ClassPathResource("all_activities.json").getInputStream();
        Event[] events = gson.fromJson(new InputStreamReader(eventStream), Event[].class);
        data = Arrays.asList(events);
    }

    @Test
    public void testNonExisting() {
        String randomWord = "µ$µ$µ$µ$µ$";

        EventSearchPredicate searchPredicate = new EventSearchPredicate();

        List<Event> result = data.stream()
                .filter(event -> searchPredicate.test(event, randomWord))
                .collect(Collectors.toList());

        assertTrue(
                "There are events that match for " + randomWord + ", which is impossible.",
                result.isEmpty()
        );
    }

    @Test
    public void testExisting() {
        Event random = Utils.getRandom(data);
        EventSearchPredicate searchPredicate = new EventSearchPredicate();

        List<Event> result = data.stream()
                .filter(event -> searchPredicate.test(event, random.getTitle()))
                .collect(Collectors.toList());

        assertTrue(
                "At least one event should be found.",
                result.isEmpty()
        );
    }
}