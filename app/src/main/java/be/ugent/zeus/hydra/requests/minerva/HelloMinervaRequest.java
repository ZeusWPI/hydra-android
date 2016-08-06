package be.ugent.zeus.hydra.requests.minerva;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.models.minerva.Hello;

/**
 * Simple request to Minerva. A potential use is testing the validity of account data and tokens.
 *
 * @author feliciaan
 */
public class HelloMinervaRequest extends MinervaRequest<Hello> {

    public HelloMinervaRequest(Context context, Activity activity) {
        super(Hello.class, context, activity);
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "hello_minerva";
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "hello";
    }

    @Override
    public long getCacheDuration() {
        return Cache.NEVER;
    }
}