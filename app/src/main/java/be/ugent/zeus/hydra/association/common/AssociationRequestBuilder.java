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

package be.ugent.zeus.hydra.association.common;

import android.content.Context;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;
import com.squareup.moshi.Json;

/**
 * Builder to create various requests to get associations from the DSA.
 *
 * @author Niko Strijbol
 */
public class AssociationRequestBuilder {

    /**
     * Raw representation of how the data comes to use from the API.
     */
    @VisibleForTesting
    static class AssociationList {
        @Json(name = "associations")
        private List<Association> associations;

        public AssociationList() {
        }

        @NonNull
        public List<Association> getAssociations() {
            if (associations == null) {
                return Collections.emptyList();
            } else {
                return associations;
            }
        }
    }

    private static class Mapper implements AssociationMap {

        private final Map<String, Association> associationMap;
        private final Set<String> associationRequested;

        private Mapper(@NonNull AssociationRequestBuilder.AssociationList list, @NonNull Set<String> requestedAssociations) {
            this.associationMap = list.getAssociations()
                    .stream()
                    .collect(Collectors.toMap(Association::getAbbreviation, Function.identity()));
            this.associationRequested = requestedAssociations;
        }

        @NonNull
        @Override
        public Association get(@Nullable String abbreviation) {
            return associationMap.computeIfAbsent(abbreviation, Association::unknown);
        }

        @Override
        public Stream<Association> associations() {
            return associationMap.values().stream();
        }

        @Override
        public boolean isRequested(@NonNull String abbreviation) {
            return associationRequested.contains(abbreviation);
        }
    }

    @VisibleForTesting
    static class RawRequest extends JsonOkHttpRequest<AssociationList> {
        private static final String FILENAME = "verenigingen";

        RawRequest(@NonNull Context context) {
            super(context, AssociationList.class);
        }

        @NonNull
        @Override
        protected String getAPIUrl() {
            return Endpoints.DSA_V4 + FILENAME;
        }

        @Override
        public Duration getCacheDuration() {
            return ChronoUnit.WEEKS.getDuration().multipliedBy(4);
        }
    }

    @NonNull
    public static Request<List<Association>> createListRequest(@NonNull Context context) {
        return new RawRequest(context)
                .map(AssociationList::getAssociations);
    }

    public static Request<Pair<AssociationMap, List<EventItem>>> createItemFilteredEventRequest(@NonNull Context context, EventFilter filter) {
        return new RawRequest(context)
                .andThen((Function<AssociationList, Request<Pair<List<EventItem>, Set<String>>>>) data -> {
                    EventRequest.Filter newFilter = filter.toRequestFilter(context, data.getAssociations());
                    Set<String> requestedAssociations = newFilter.getRequestedAssociations();
                    return EventRequest.createItemRequest(context, newFilter).map(e -> Pair.create(e, requestedAssociations));
                })
                .map(pair -> {
                    AssociationMap map = new Mapper(pair.first, pair.second.second);
                    return Pair.create(map, pair.second.first);
                });
    }

    public static Request<Pair<AssociationMap, List<Event>>> createFilteredEventRequest(@NonNull Context context) {
        return new RawRequest(context)
                .andThen((Function<AssociationList, Request<Pair<List<Event>, Set<String>>>>) data -> {
                    EventFilter eventFilter = new EventFilter();
                    EventRequest.Filter newFilter = eventFilter.toRequestFilter(context, data.getAssociations());
                    Set<String> requestedAssociations = newFilter.getRequestedAssociations();
                    return EventRequest.createRequest(context, newFilter).map(e -> Pair.create(e, requestedAssociations));
                })
                .map(pair -> {
                    AssociationMap map = new Mapper(pair.first, pair.second.second);
                    return Pair.create(map, pair.second.first);
                });
    }
}