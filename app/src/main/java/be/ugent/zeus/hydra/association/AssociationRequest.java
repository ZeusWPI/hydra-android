/*
 * Copyright (c) 2022 The Hydra authors
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

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import androidx.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;

/**
 * Builder to create various requests to get associations from the DSA.
 *
 * @author Niko Strijbol
 */
public class AssociationRequest extends JsonOkHttpRequest<AssociationList> {

    private static final String FILENAME = "verenigingen";

    @VisibleForTesting
    AssociationRequest(@NonNull Context context) {
        super(context, AssociationList.class);
    }

    @NonNull
    @Override
    protected String apiUrl() {
        return Endpoints.DSA_V4 + FILENAME;
    }

    @Override
    public Duration cacheDuration() {
        return ChronoUnit.WEEKS.getDuration().multipliedBy(4);
    }

    public record EventsAndAssociations(
            AssociationMap associations,
            List<Event> events
    ) {
    }

    public record EventItemsAndAssociations(
            AssociationMap associations,
            List<EventItem> events
    ) {
    }

    /**
     * Returns a request object that retrieves a list of associations.
     *
     * @param context The context.
     * @return A request object that retrieves a list of associations.
     */
    public static Request<List<Association>> associationListRequest(@NonNull Context context) {
        return new AssociationRequest(context)
                .map(AssociationList::associations);
    }

    /**
     * Creates a filtered event item request.
     * <p>
     * This contains all events that satisfy the filter, with an association map.
     *
     * @param context The context of the application.
     * @param filter  The event filter to apply.
     * @return A request object containing a pair of an association map and a list of event items.
     */
    public static Request<EventItemsAndAssociations> filteredEventItemRequest(@NonNull Context context, @NonNull EventFilter filter) {
        return filteredEventRequest(context, filter)
                .map(results -> {
                    var eventItems = EventItem.fromEvents(results.events);
                    return new EventItemsAndAssociations(results.associations, eventItems);
                });
    }

    /**
     * Creates a filtered events request.
     * <p>
     * This contains all events that satisfy the filter, with an association map.
     *
     * @param context        The context of the application.
     * @param optionalFilter The event filter to apply.
     * @return A request object containing a pair of an association map and a list of events.
     */
    public static Request<EventsAndAssociations> filteredEventRequest(@NonNull Context context, @Nullable EventFilter optionalFilter) {
        var filter = Objects.requireNonNullElse(optionalFilter, new EventFilter());
        return associationListRequest(context)
                .andThen(associations -> {
                    var eventRequestFilter = filter.toRequestFilter(context, associations);
                    var requestedAssociations = eventRequestFilter.getRequestedAssociations();
                    var associationMap = new AssociationMap(associations, requestedAssociations);
                    return EventRequest.eventRequest(context, eventRequestFilter)
                            .map(events -> new EventsAndAssociations(associationMap, events));
                });
    }
}