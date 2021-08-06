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

package be.ugent.zeus.hydra.feed.commands;

import android.os.Bundle;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.CardIdentifier;

/**
 * Used to report that the user dismissed a card to the analytics.
 *
 * @author Niko Strijbol
 */
class DismissalEvent implements Event {

    private final String dismissalType;
    private final CardIdentifier identifier;

    DismissalEvent(String association) {
        this.dismissalType = "association";
        this.identifier = new CardIdentifier(Card.Type.ACTIVITY, association);
    }

    DismissalEvent(CardIdentifier identifier) {
        this.dismissalType = "individual_card";
        this.identifier = identifier;
    }

    DismissalEvent(@Card.Type int type) {
        this.dismissalType = "card_type";
        this.identifier = new CardIdentifier(type, "all_cards");
    }


    @Nullable
    @Override
    public Bundle getParams() {
        Bundle bundle = new Bundle();
        BaseEvents.Params names = Reporting.getEvents().params();
        bundle.putString(names.dismissalType(), this.dismissalType);
        bundle.putInt(names.cardType(), identifier.getCardType());
        bundle.putString(names.cardIdentifier(), identifier.getIdentifier());
        return bundle;
    }

    @Nullable
    @Override
    public String getEventName() {
        return Reporting.getEvents().cardDismissal();
    }
}
