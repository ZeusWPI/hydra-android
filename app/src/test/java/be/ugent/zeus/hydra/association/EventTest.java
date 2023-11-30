/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.association;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

import static be.ugent.zeus.hydra.testing.Assert.assertRecordParcelable;
import static org.junit.Assert.*;

public class EventTest {
    
    @Test
    public void parcelable() {
        assertRecordParcelable(Utils.generate(Event.class));
    }

    @Test
    public void shouldReturnNull_whenEndIsNull() {
        Event event = Utils.generate(Event.class).withEnd(null);
        assertNull(event.localEnd());
    }

    @Test
    public void shouldReturnLocal_whenEndIsNotNull() {
        OffsetDateTime offsetDateTime = Utils.generate(OffsetDateTime.class);
        Event event = Utils.generate(Event.class).withEnd(offsetDateTime);
        LocalDateTime localDateTime = event.localEnd();
        assertNotNull(localDateTime);
        assertEquals(DateUtils.toLocalDateTime(offsetDateTime), localDateTime);
    }

    @Test
    public void shouldReturnLocal_whenStart() {
        OffsetDateTime offsetDateTime = Utils.generate(OffsetDateTime.class);
        Event event = Utils.generate(Event.class).withStart(offsetDateTime);
        LocalDateTime localDateTime = event.localStart();
        assertEquals(DateUtils.toLocalDateTime(offsetDateTime), localDateTime);
    }

    @Test
    public void shouldHaveUniqueIdentifier_whenDataIsUnique() {
        long resulting = Utils.generate(Event.class, 10)
                .map(Event::identifier)
                .distinct()
                .count();
        assertEquals(10, resulting);
    }

    @Test
    public void shouldBeSortedOnStartDate() {
        List<Event> events = Utils.generate(Event.class, 10).collect(Collectors.toList());
        List<Event> expected = new ArrayList<>(events);
        expected.sort(Comparator.comparing(Event::start));
        List<Event> actual = new ArrayList<>(events);
        Collections.sort(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotHaveLocation_whenThereIsNoLocation() {
        Event event = Utils.generate(Event.class).withLocation(null);
        assertFalse(event.hasLocation());
    }

    @Test
    public void shouldNotHaveLocation_whenThereIsEmptyLocation() {
        Event event = Utils.generate(Event.class).withLocation("");
        assertFalse(event.hasLocation());
    }

    @Test
    public void shouldHaveLocation_whenThereIsLocation() {
        Event event = Utils.generate(Event.class);
        assertTrue(event.hasLocation());
    }

    @Test
    public void shouldHavePreciesLocation_whenThereAreCoordinates() {
        Event event = Utils.generate(Event.class);
        assertTrue(event.hasPreciseLocation());
    }

    @Test
    public void shouldNotHaveUrl_whenThereIsNoUrl() {
        Event event = Utils.generate(Event.class).withUrl(null);
        assertFalse(event.hasUrl());
    }

    @Test
    public void shouldNotHaveUrl_whenThereIsEmptyUrl() {
        Event event = Utils.generate(Event.class, "url").withUrl("");
        assertFalse(event.hasUrl());
    }

    @Test
    public void shouldHaveUrl_whenThereIsUrl() {
        Event event = Utils.generate(Event.class);
        assertTrue(event.hasUrl());
    }
}
