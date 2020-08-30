package be.ugent.zeus.hydra.feed.commands;

import android.os.Bundle;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.association.Association;
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

    DismissalEvent(Association association) {
        this.dismissalType = "association";
        this.identifier = new CardIdentifier(Card.Type.ACTIVITY, association.getInternalName());
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
