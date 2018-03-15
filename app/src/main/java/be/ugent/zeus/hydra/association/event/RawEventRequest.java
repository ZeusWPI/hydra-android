package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.request.CacheableRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import java8.util.function.Function;

import java.util.List;

/**
 * Get the events for all associations. This will get all events returned by the API, without any filtering.
 *
 * You probably want {@link #cachedFilteredSortedRequest(Context)} instead.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class RawEventRequest extends JsonSpringRequest<Event[]> implements CacheableRequest<Event[]> {

    private static final String FILENAME = "all_activities.json";

    @SuppressWarnings("WeakerAccess")
    public RawEventRequest() {
        super(Event[].class);
    }

    @NonNull
    public String getCacheKey() {
        return FILENAME;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_API_URL_3 + FILENAME;
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR;
    }

    /**
     * Transform the {@link RawEventRequest} by applying:
     * - {@link Requests#cachedList(Context, CacheableRequest)}
     * - {@link Request#map(Function)} with {@link DisabledEventRemover}
     * - {@link Request#map(Function)} with {@link EventSorter}
     *
     * @param context The context.
     *
     * @return The modified request.
     */
    public static Request<List<Event>> cachedFilteredSortedRequest(Context context) {
        return Requests.cachedList(context, new RawEventRequest())
                .map(new DisabledEventRemover(context))
                .map(new EventSorter());
    }
}