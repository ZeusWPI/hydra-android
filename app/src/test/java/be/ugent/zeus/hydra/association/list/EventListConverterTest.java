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

package be.ugent.zeus.hydra.association.list;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventList;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.Utils;
import com.squareup.moshi.Moshi;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
public class EventListConverterTest {

    private List<Event> data;

    @Before
    public void setUp() throws IOException {
        Moshi moshi = InstanceProvider.getMoshi();
        data = Utils.readJson(moshi, "activiteiten.json", EventList.class).getPage().getEntries();
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
        for (EventItem item : result) {
            if (previousWasLastOfSection) {
                assertTrue(item.isHeader());
                previousWasHeader = true;
                previousWasLastOfSection = false;
            } else if (previousWasHeader) {
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