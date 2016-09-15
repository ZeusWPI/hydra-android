package be.ugent.zeus.hydra.requests.sko;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.sko.Timeline;

/**
 * @author Niko Strijbol
 */
public class TimelineRequest extends SkoRequest<Timeline> {

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
}