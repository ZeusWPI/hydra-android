package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;
import java8.util.function.Function;
import org.threeten.bp.Duration;

import java.util.List;

/**
 * Get the events for all associations. This will get all events returned by the API, without any filtering.
 *
 * You probably want {@link #cachedFilteredSortedRequest(Context)} instead.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class RawEventRequest extends JsonOkHttpRequest<List<Event>> {

    private static final String FILENAME = "all_activities.json";

    RawEventRequest(Context context) {
        super(context, listToken(Event.class));
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_API_URL_3 + FILENAME;
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofHours(1);
    }

    /**
     * Transform the {@link RawEventRequest} by applying:
     * - {@link Request#map(Function)} with {@link DisabledEventRemover}
     * - {@link Request#map(Function)} with {@link EventSorter}
     *
     * @param context The context.
     *
     * @return The modified request.
     */
    public static Request<List<Event>> cachedFilteredSortedRequest(Context context) {
        return new RawEventRequest(context)
                .map(new DisabledEventRemover(context))
                .map(new EventSorter());
    }
}