package be.ugent.zeus.hydra.data.network.requests.association;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity;
import java8.util.Comparators;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A request that filters the events according to the user's preferences. The events will also be sorted according to
 * their start date.
 *
 * @author Niko Strijbol
 */
public class FilteredEventRequest implements Request<List<Event>> {

    private final Request<List<Event>> request;
    private final Context context;

    public FilteredEventRequest(Context context, Request<List<Event>> request) {
        this.request = request;
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public List<Event> performRequest() throws RequestFailureException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> disabled = preferences.getStringSet(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING, Collections.emptySet());

        return StreamSupport.stream(request.performRequest())
                .filter(e -> !disabled.contains(e.getAssociation().internalName()))
                .sorted(Comparators.comparing(Event::getStart))
                .collect(Collectors.toList());
    }
}