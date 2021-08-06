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

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.association.event.Event;

/**
 * Convert events to EventItems. The list of events MUST be sorted by start date.
 */
class EventListConverter implements Function<List<Event>, List<EventItem>> {

    @Override
    public List<EventItem> apply(List<Event> events) {

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        return events.stream()
                .collect(Collectors.groupingBy(event -> event.getStart().toLocalDate()))
                .entrySet()
                .stream()
                .flatMap(this::convert)
                .sorted()
                .collect(Collectors.toList());
    }

    private Stream<EventItem> convert(Map.Entry<LocalDate, List<Event>> entry) {
        List<Event> list = entry.getValue();
        EventItem header = new EventItem(entry.getKey());
        Stream<EventItem> events = list.subList(0, list.size() - 1).stream().map(event -> new EventItem(event, false));
        EventItem last = new EventItem(list.get(list.size() - 1), true);

        return Stream.concat(
                Stream.of(header),
                Stream.concat(events, Stream.of(last))
        );
    }
}
