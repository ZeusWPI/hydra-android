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

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventList;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;


/**
 * Get the events for all associations.
 * <p> 
 * The general event flow is like this:
 * <p> 
 * 1. We get the list of associations from the server.
 * 2. Use the filter to update the blacklist.
 * 3. Use the blacklist to get a whitelist.
 * 4. Use the whitelist to make an event request.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
class EventRequest extends JsonOkHttpRequest<EventList> {
    private static final String FILENAME = "activiteiten";

    public static final class Filter {
        private final Set<String> requestedAssociations;
        private OffsetDateTime after;
        private OffsetDateTime before;
        private String term;
        
        public Filter(Set<String> requestedAssociations) {
            this.requestedAssociations = requestedAssociations;
        }

        public void setAfter(OffsetDateTime after) {
            this.after = after;
        }

        public void setBefore(OffsetDateTime before) {
            this.before = before;
        }

        public void setTerm(String term) {
            this.term = term;
        }
        
        public Set<String> getRequestedAssociations() {
            return requestedAssociations;
        }
    }

    private final Filter filter;

    public EventRequest(Context context, Filter filter) {
        super(context, EventList.class);
        this.filter = filter;
    }
    
    public static Request<List<EventItem>> createItemRequest(Context context, Filter filter) {
        return new EventRequest(context, filter)
                .map(e -> e.getPage().getEntries())
                .map(new EventListConverter());
    }
    
    public static Request<List<Event>> createRequest(Context context, Filter filter) {
        return new EventRequest(context, filter)
                .map(e -> e.getPage().getEntries());
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        Uri.Builder uri = Uri.parse(Endpoints.DSA_V4 + FILENAME).buildUpon();
        
        for (String association : filter.requestedAssociations) {
            uri.appendQueryParameter("association[]", association);
        }
        
        if (filter.requestedAssociations.isEmpty()) {
            uri.appendQueryParameter("association[]", "");
        }
        
        if (filter.after != null) {
            uri.appendQueryParameter("start_time", filter.after.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
        if (filter.before != null) {
            uri.appendQueryParameter("end_time", filter.before.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
        if (filter.term != null) {
            uri.appendQueryParameter("search_string", filter.term);
        }
        
        String t = uri.appendQueryParameter("page_size", "50").build().toString();
        Log.d("TAG", "getAPIUrl: " + t);
        return t;
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofHours(1);
    }
}