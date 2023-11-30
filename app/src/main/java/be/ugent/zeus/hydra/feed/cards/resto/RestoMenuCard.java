/*
 * Copyright (c) 2021 The Hydra authors
 * Copyright (c) 2022 Niko Strijbol
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

package be.ugent.zeus.hydra.feed.cards.resto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;

/**
 * Home card for {@link RestoMenu}.
 *
 * @author Niko Strijbol
 * @author silox
 */
class RestoMenuCard extends Card {

    // From 10:30 h to 14:30 h we are more interested in the menu.
    private static final LocalDateTime interestStart = LocalDateTime.now().withHour(10).withMinute(30);
    private static final LocalDateTime interestEnd = LocalDateTime.now().withHour(14).withMinute(30);

    public final RestoMenu restoMenu;
    public final RestoChoice restoChoice;
    @MenuTable.DisplayKind
    private final int feedRestoKind;

    RestoMenuCard(RestoMenu restoMenu, RestoChoice choice, @MenuTable.DisplayKind int feedRestoKind) {
        this.restoMenu = restoMenu;
        this.restoChoice = choice;
        this.feedRestoKind = feedRestoKind;
    }

    @Override
    public int priority() {
        LocalDateTime now = LocalDateTime.now();
        int duration = (int) ChronoUnit.DAYS.between(now.toLocalDate(), restoMenu.date());
        if (now.isAfter(interestStart) && now.isBefore(interestEnd)) {
            return Math.max(PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp((int) ((duration - 0.5) * 24), 0, 504)) - 5;
        } else {
            return Math.max(PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp((int) ((duration - 0.5) * 24), 0, 504)) + 3;
        }

    }

    @Override
    public String identifier() {
        // Two resto's for the same day are equal, regardless of the resto.
        return restoMenu.date().toString();
    }

    @Override
    public int cardType() {
        return Card.Type.RESTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoMenuCard that = (RestoMenuCard) o;
        return Objects.equals(restoMenu, that.restoMenu) &&
                Objects.equals(restoChoice, that.restoChoice) &&
                Objects.equals(feedRestoKind, that.feedRestoKind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restoMenu, restoChoice, feedRestoKind);
    }
}