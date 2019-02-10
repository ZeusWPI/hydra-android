package be.ugent.zeus.hydra.sko.timeline;

import android.content.Context;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import org.threeten.bp.Duration;

/**
 * Request for posts.
 *
 * @author Niko Strijbol
 */
class TimelineRequest extends JsonArrayRequest<TimelinePost> {

    TimelineRequest(Context context) {
        super(context, TimelinePost.class);
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofMinutes(15);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.SKO_LIVE + "timeline.json";
    }
}