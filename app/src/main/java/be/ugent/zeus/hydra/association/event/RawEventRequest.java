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
 * 
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

    /**
     * Transform by applying:
     * - {@link Request#map(Function)} with {@link EventSorter}
     *
     * @param context The context.
     * @return The modified request.
     */
    public static Request<List<Event>> create(Context context, Filter filter) {
        return new RawEventRequest(context, filter)
                .map(eventList -> eventList.getPage().getEntries());
    }
}