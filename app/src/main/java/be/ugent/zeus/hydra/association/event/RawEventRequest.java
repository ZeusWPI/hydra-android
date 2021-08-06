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

package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import be.ugent.zeus.hydra.association.list.Filter;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;


/**
 * Get the events for all associations. This will get all events returned by the API, without any filtering.
 * <p>
 * You probably want {@link #create(Context, Filter)} instead.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class RawEventRequest extends JsonOkHttpRequest<EventList> {

    private static final String FILENAME = "activiteiten";

    private final Filter params;

    RawEventRequest(Context context, Filter params) {
        super(context, EventList.class);
        this.params = params;
        Log.d("EVENT", "RawEventRequest: " + params);
    }

    /**
     * Transform by applying:
     * - {@link Request#map(Function)}.
     *
     * @param context The context.
     * @return The modified request.
     */
    public static Request<List<Event>> create(Context context, Filter filter) {
        return new RawEventRequest(context, filter)
                .map(eventList -> eventList.getPage().getEntries());
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        Uri.Builder uri = Uri.parse(Endpoints.DSA_V4 + FILENAME).buildUpon();
        String t = params.appendParams(uri).appendQueryParameter("page_size", "50").build().toString();
        Log.d("TAG", "getAPIUrl: " + t);
        return t;
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofHours(1);
    }
}