package be.ugent.zeus.hydra.association.event.list;

import org.junit.Test;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Niko Strijbol
 */
public class EventSearchFilterTest {

    @Test
    public void testAllHeaders() {
        LocalDate now = LocalDate.now();
        List<EventItem> headers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            headers.add(new EventItem(now.plusDays(i)));
        }

        EventSearchFilter filter = new EventSearchFilter();
        List<EventItem> result = filter.apply(headers);

        assertThat(result, is(empty()));
    }

    @Test
    public void testMixed() {
        LocalDate now = LocalDate.now();
        List<EventItem> headers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            headers.add(new EventItem(now.plusDays(i)));
        }
        for (int i = 0; i < 5; i++) {
            headers.add(generate(EventItem.class, "header"));
        }

        List<EventItem> expected = new ArrayList<>(headers);
        expected.subList(0, 9).clear();

        EventSearchFilter filter = new EventSearchFilter();
        List<EventItem> result = filter.apply(headers);

        assertEquals(expected, result);
    }

    @Test
    public void testNoHeaders() {
        List<EventItem> items = generate(EventItem.class, 10, "header").collect(Collectors.toList());

        EventSearchFilter filter = new EventSearchFilter();
        List<EventItem> result = filter.apply(items);

        assertEquals(items, result);
    }

    @Test
    public void testEmpty() {
        List<EventItem> items = Collections.emptyList();
        EventSearchFilter filter = new EventSearchFilter();
        List<EventItem> result = filter.apply(items);
        assertThat(result, is(empty()));
    }
}