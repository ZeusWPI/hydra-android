package be.ugent.zeus.hydra.requests;

import android.content.Context;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.minerva.Hello;

/**
 * Created by feliciaan on 20/06/16.
 */
public class HelloMinervaRequest extends MinervaRequest<Hello> {

    public HelloMinervaRequest(Context context) {
        super(Hello.class, context);
    }

    @Override
    public String getCacheKey() {
        return "hello_minerva";
    }

    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "hello";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_HOUR;
    }
}
