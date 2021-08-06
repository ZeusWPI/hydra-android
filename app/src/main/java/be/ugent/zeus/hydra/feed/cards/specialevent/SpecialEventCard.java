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

package be.ugent.zeus.hydra.feed.cards.specialevent;

import java.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.specialevent.SpecialEvent;

/**
 * Home card for a {@link SpecialEvent}.
 *
 * @author Niko Strijbol
 * @author silox
 */
class SpecialEventCard extends Card {

    private final SpecialEvent specialEvent;

    SpecialEventCard(SpecialEvent specialEvent) {
        this.specialEvent = specialEvent;
    }

    SpecialEvent getSpecialEvent() {
        return specialEvent;
    }

    @Override
    public int getPriority() {
        //We get the complement, as the server assumes 1000 = highest priority. This is for
        //historical reasons.
        return PriorityUtils.FEED_MAX_VALUE - specialEvent.getPriority() - 2 * PriorityUtils.FEED_SPECIAL_SHIFT;
    }

    @Override
    public String getIdentifier() {
        return String.valueOf(specialEvent.getId());
    }

    @Override
    public int getCardType() {
        return Card.Type.SPECIAL_EVENT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialEventCard that = (SpecialEventCard) o;
        return Objects.equals(specialEvent, that.specialEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specialEvent);
    }
}