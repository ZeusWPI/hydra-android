package be.ugent.zeus.hydra.association.event.list;

import android.annotation.SuppressLint;

import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.Utils;
import com.squareup.moshi.Types;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@SuppressLint("NewApi")
public class EventSearchPredicateTest {

    private List<Event> data;

    @Before
    public void setUp() throws IOException {
        data = Utils.readJson(InstanceProvider.getMoshi(), "all_activities.json",
                Types.newParameterizedType(List.class, Event.class));
    }

    @Test
    @Ignore
    public void testNonExisting() {
//        String randomWord = "µ$µ$µ$µ$µ$";
//
//        EventSearchPredicate searchPredicate = new EventSearchPredicate();
//
//        List<Event> result = data.stream()
//                .filter(event -> searchPredicate.test(event, randomWord))
//                .collect(Collectors.toList());
//
//        assertTrue(
//                "There are events that match for " + randomWord + ", which is impossible.",
//                result.isEmpty()
//        );
    }

    @Test
    @Ignore
    public void testExisting() {
//        Event random = Utils.getRandom(data);
//        EventSearchPredicate searchPredicate = new EventSearchPredicate();
//
//        List<Event> result = data.stream()
//                .filter(event -> searchPredicate.test(event, random.getTitle()))
//                .collect(Collectors.toList());
//
//        assertTrue(
//                "At least one event should be found.",
//                result.isEmpty()
//        );
    }
}