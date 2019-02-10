package be.ugent.zeus.hydra.association.event;

import android.content.Context;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Request;
import java9.util.function.Function;
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
public class RawEventRequest extends JsonArrayRequest<Event> {

    private static final String FILENAME = "all_activities.json";

    RawEventRequest(Context context) {
        super(context, Event.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_V3 + FILENAME;
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
                .map(new DisabledEventRemover(context))
                .map(new EventSorter());
    }
}