package be.ugent.zeus.hydra.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.sko.Timeline;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * @author Niko Strijbol
 */
public class TimelineRequest extends CacheableRequest<Timeline> {

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