package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;

/**
 * Get the events for all associations. This will get all events returned by the API, without any filtering.
 *
 * You probably want {@link #cachedFilteredSortedRequest(Context)} instead.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class RawEventRequest extends JsonOkHttpRequest<EventList> {

    private static final String FILENAME = "activiteiten";

    RawEventRequest(Context context) {
        super(context, EventList.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_V4 + FILENAME + "?page_size=100";
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofHours(1);
    }

    /**
     * Transform by applying:
     * - {@link Request#map(Function)} with {@link DisabledEventRemover}
     * - {@link Request#map(Function)} with {@link EventSorter}
     *
     * @param context The context.
     *
     * @return The modified request.
     */
    public static Request<List<Event>> cachedFilteredSortedRequest(Context context) {
        return new RawEventRequest(context)
                .map(eventList -> eventList.getPage().getEntries())
                .map(new DisabledEventRemover(context));
    }
}