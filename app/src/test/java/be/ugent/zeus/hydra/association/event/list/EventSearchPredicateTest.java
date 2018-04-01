package be.ugent.zeus.hydra.association.event.list;

import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.Utils;
import com.squareup.moshi.Types;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Niko Strijbol
 */
public class EventSearchPredicateTest {

    private List<EventItem> data;

    @Before
    public void setUp() throws IOException {
        List<Event> events = Utils.readJson(InstanceProvider.getMoshi(), "all_activities.json",
                Types.newParameterizedType(List.class, Event.class));
        data = new EventListConverter().apply(events).stream()
                .filter(EventItem::isItem) // Remove headers, since we don't need them.
                .collect(Collectors.toList());
    }

    @Test
    public void testNonExisting() {
        String randomWord = "µ$µ$µ$µ$µ$";

        EventSearchPredicate searchPredicate = new EventSearchPredicate();

        List<EventItem> result = data.stream()
                .filter(event -> searchPredicate.test(event, randomWord))
                .collect(Collectors.toList());

        assertThat(result, is(empty()));
    }

    @Test
    public void testTitle() {
        EventItem random = Utils.getRandom(data);
        EventSearchPredicate searchPredicate = new EventSearchPredicate();

        List<EventItem> result = data.stream()
                .filter(event -> searchPredicate.test(event, random.getItem().getTitle().toLowerCase()))
                .collect(Collectors.toList());

        assertThat(result, hasItem(random));
    }

    @Test
    public void testHeaders() {
        List<EventItem> items = generate(EventItem.class, 10, "event").collect(Collectors.toList());
        EventSearchPredicate searchPredicate = new EventSearchPredicate();

        List<EventItem> result = items.stream()
                .filter(e -> searchPredicate.test(e, "test"))
                .collect(Collectors.toList());

        assertEquals(items, result);
    }

    @Test
    public void testAssociationDisplayName() {
        EventItem random = Utils.getRandom(data);
        EventSearchPredicate searchPredicate = new EventSearchPredicate();

        List<EventItem> result = data.stream()
                .filter(e -> searchPredicate.test(e, random.getItem().getAssociation().getDisplayName().toLowerCase()))
                .collect(Collectors.toList());

        assertThat(result, hasItem(random));
    }

    @Test
    public void testAssociationFullName() throws Throwable {
        // Get first where the full name is not null.
        EventItem random = data.stream()
                .filter(d -> d.getItem().getAssociation().getFullName() != null)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No data was found."));
        EventSearchPredicate searchPredicate = new EventSearchPredicate();

        List<EventItem> result = data.stream()
                .filter(e -> searchPredicate.test(e, random.getItem().getAssociation().getFullName().toLowerCase()))
                .collect(Collectors.toList());

        assertThat(result, hasItem(random));
    }

    @Test
    public void testAssociationInternalName() {
        EventItem random = Utils.getRandom(data);
        EventSearchPredicate searchPredicate = new EventSearchPredicate();

        List<EventItem> result = data.stream()
                .filter(e -> searchPredicate.test(e, random.getItem().getAssociation().getInternalName()))
                .collect(Collectors.toList());

        assertThat(result, hasItem(random));
    }
}