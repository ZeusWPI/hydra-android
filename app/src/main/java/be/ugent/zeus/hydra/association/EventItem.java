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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Data structure for a list of events.
 * The event contains an item, a header or a footer, but only one of the elements.
 * If it is an item, it contains some additional metadata.
 *
 * @author Niko Strijbol
 */
public record EventItem(
        @Nullable Event event,
        @Nullable LocalDate header,
        boolean isLastOfSection
) implements Comparable<EventItem> {

    public static EventItem create(Event event) {
        return new EventItem(event, null, false);
    }

    public static EventItem create(LocalDate header) {
        return new EventItem(null, header, false);
    }

    public static EventItem create(Event event, boolean isLastOfSection) {
        return new EventItem(event, null, isLastOfSection);
    }

    public boolean isHeader() {
        return header != null;
    }

    public boolean isItem() {
        return event != null;
    }

    @Override
    public boolean isLastOfSection() {
        if (!isItem()) {
            throw new IllegalStateException("Can only be used if the EventItem contains an item.");
        }
        return isLastOfSection;
    }

    @Override
    public Event event() {
        if (!isItem()) {
            throw new IllegalStateException("Can only be used if the EventItem contains an item.");
        }
        return event;
    }

    @Override
    public LocalDate header() {
        if (!isHeader()) {
            throw new IllegalStateException("Can only be used if the EventItem contains a header.");
        }
        return header;
    }

    public OffsetDateTime date() {
        if (isItem()) {
            return event().start();
        } else {
            return header().atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        }
    }

    @Override
    public int compareTo(EventItem o) {
        return date().compareTo(o.date());
    }

    @NonNull
    public static List<EventItem> fromEvents(@NonNull List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        return events.stream()
                .collect(Collectors.groupingBy(event -> event.start().toLocalDate()))
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    List<Event> list = entry.getValue();
                    EventItem header = EventItem.create(entry.getKey());
                    Stream<EventItem> events1 = list.subList(0, list.size() - 1).stream().map(EventItem::create);
                    EventItem last = EventItem.create(list.get(list.size() - 1), true);

                    return Stream.concat(
                            Stream.of(header),
                            Stream.concat(events1, Stream.of(last))
                    );
                })
                .sorted()
                .collect(Collectors.toList());
    }
}
