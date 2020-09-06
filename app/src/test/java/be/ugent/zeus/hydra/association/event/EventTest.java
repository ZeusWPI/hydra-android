package be.ugent.zeus.hydra.association.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import java9.util.Lists;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import de.jaehrig.gettersetterverifier.GetterSetterVerifier;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;

import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
public class EventTest extends ModelTest<Event> {
    public EventTest() {
        super(Event.class);
    }

    @Test
    public void shouldReturnNull_whenEndIsNull() {
        Event event = Utils.generate(Event.class, "end");
        assertNull(event.getLocalEnd());
    }

    @Test
    public void shouldReturnLocal_whenEndIsNotNull() {
        OffsetDateTime offsetDateTime = Utils.generate(OffsetDateTime.class);
        Event event = Utils.generate(Event.class, "end");
        Utils.setField(event, "end", offsetDateTime);
        LocalDateTime localDateTime = event.getLocalEnd();
        assertNotNull(localDateTime);
        assertEquals(DateUtils.toLocalDateTime(offsetDateTime), localDateTime);
    }

    @Test
    public void shouldReturnLocal_whenStart() {
        OffsetDateTime offsetDateTime = Utils.generate(OffsetDateTime.class);
        Event event = Utils.generate(Event.class, "start");
        Utils.setField(event, "start", offsetDateTime);
        LocalDateTime localDateTime = event.getLocalStart();
        assertEquals(DateUtils.toLocalDateTime(offsetDateTime), localDateTime);
    }

    @Test
    public void gettersAndSetters() {
        GetterSetterVerifier.forClass(Event.class)
                .excludeFields("localStart", "localEnd", "identifier")
                .verify();
    }

    @Test
    public void shouldHaveUniqueIdentifier_whenDataIsUnique() {
        long resulting = Utils.generate(Event.class, 10)
                .map(Event::getIdentifier)
                .distinct()
                .count();
        assertEquals(10, resulting);
    }

    @Test
    public void shouldBeSortedOnStartDate() {
        List<Event> events = Utils.generate(Event.class, 10).collect(Collectors.toList());
        List<Event> expected = new ArrayList<>(events);
        Lists.sort(expected, Comparator.comparing(Event::getStart));
        List<Event> actual = new ArrayList<>(events);
        Collections.sort(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotHaveLocation_whenThereIsNoLocation() {
        Event event = Utils.generate(Event.class, "location");
        assertFalse(event.hasLocation());
        Utils.setField(event, "location", "");
    }

    @Test
    public void shouldNotHaveLocation_whenThereIsEmptyLocation() {
        Event event = Utils.generate(Event.class, "location");
        Utils.setField(event, "location", "");
        assertFalse(event.hasLocation());
    }

    @Test
    public void shouldHaveLocation_whenThereIsLocation() {
        Event event = Utils.generate(Event.class);
        assertTrue(event.hasLocation());
    }

    @Test
    public void shouldNotHavePreciesLocation_whenThereAreNoCoordinates() {
        Event event = Utils.generate(Event.class, "latitude", "longitude");
        assertFalse(event.hasPreciseLocation());
    }

    @Test
    public void shouldHavePreciesLocation_whenThereAreCoordinates() {
        Event event = Utils.generate(Event.class);
        assertTrue(event.hasPreciseLocation());
    }

    @Test
    public void shouldNotHaveUrl_whenThereIsNoUrl() {
        Event event = Utils.generate(Event.class, "url");
        assertFalse(event.hasUrl());
    }

    @Test
    public void shouldNotHaveUrl_whenThereIsEmptyUrl() {
        Event event = Utils.generate(Event.class, "url");
        Utils.setField(event, "url", "");
        assertFalse(event.hasUrl());
    }

    @Test
    public void shouldHaveUrl_whenThereIsUrl() {
        Event event = Utils.generate(Event.class);
        assertTrue(event.hasUrl());
    }
}
