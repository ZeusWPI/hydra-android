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

package be.ugent.zeus.hydra.association.common;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;

import be.ugent.zeus.hydra.association.event.Event;

/**
 * Data structure for a list of events. The contains an item, a header or a footer, but only one of the elements.
 * If it is an item, it contains some additional metadata.
 *
 * @author Niko Strijbol
 */
public final class EventItem implements Comparable<EventItem> {

    private final Event event;
    private final LocalDate header;

    private boolean isLastOfSection;

    private EventItem(Event event, LocalDate header) {
        this.event = event;
        this.header = header;
    }

    public EventItem(Event event, boolean isLastOfSection) {
        this(event, null);
        this.isLastOfSection = isLastOfSection;
    }

    public EventItem(LocalDate header) {
        this(null, header);
    }

    public boolean isHeader() {
        return header != null;
    }

    public boolean isItem() {
        return event != null;
    }

    public boolean isLastOfSection() {
        if (!isItem()) {
            throw new IllegalStateException("Can only be used if the EventItem contains an item.");
        }
        return isLastOfSection;
    }

    public Event getItem() {
        if (!isItem()) {
            throw new IllegalStateException("Can only be used if the EventItem contains an item.");
        }
        return event;
    }

    public LocalDate getHeader() {
        if (!isHeader()) {
            throw new IllegalStateException("Can only be used if the EventItem contains a header.");
        }
        return header;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventItem eventItem = (EventItem) o;
        return isLastOfSection == eventItem.isLastOfSection &&
                Objects.equals(event, eventItem.event) &&
                Objects.equals(header, eventItem.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, header, isLastOfSection);
    }
    
    public OffsetDateTime getDate() {
        if (isItem()) {
            return getItem().getStart();
        } else {
            return getHeader().atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        }
    }

    @Override
    public int compareTo(EventItem o) {
        return getDate().compareTo(o.getDate());
    }
}
