package be.ugent.zeus.hydra.feed.cards.event;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.association.AssociationListRequest;
import be.ugent.zeus.hydra.association.AssociationMap;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.RawEventRequest;
import be.ugent.zeus.hydra.association.list.Filter;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.utils.PreferencesUtils;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;

/**
 * Home feed request for association events. We only display events between now and 1 months from now.
 *
 * @author Niko Strijbol
 */
public class EventRequest extends HideableHomeFeedRequest {

    private final Request<Pair<List<Event>, AssociationMap>> request;

    public EventRequest(Context context, DismissalDao dismissalDao) {
        super(dismissalDao);
        this.request = RawEventRequest.create(context, create(context))
                .andThen(AssociationListRequest.create(context));
    }
    
    private static Filter create(Context context) {
        Filter filter = new Filter();
        filter.addStoredWhitelist(context);
        return filter;
    }

    @Override
    public int getCardType() {
        return Card.Type.ACTIVITY;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        return request.execute(args)
                .map(events -> events.first.stream()
                        .map(event -> new EventCard(event, events.second)));
    }
}