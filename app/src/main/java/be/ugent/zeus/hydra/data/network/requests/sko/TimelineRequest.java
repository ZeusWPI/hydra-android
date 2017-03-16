package be.ugent.zeus.hydra.data.network.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.caching.Cache;
import be.ugent.zeus.hydra.data.models.sko.Timeline;

/**
 * @author Niko Strijbol
 */
public class TimelineRequest extends be.ugent.zeus.hydra.data.network.JsonSpringRequest<Timeline> implements be.ugent.zeus.hydra.data.network.caching.CacheableRequest<Timeline> {

    private static final String BASE_URL = "http://live.studentkickoff.be/";

    public TimelineRequest() {
        super(Timeline.class);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "timeline.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_HOUR;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return BASE_URL + getCacheKey();
    }
}