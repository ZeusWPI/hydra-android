package be.ugent.zeus.hydra.requests.minerva;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.models.minerva.Hello;

/**
 * Created by feliciaan on 20/06/16.
 */
public class HelloMinervaRequest extends MinervaRequest<Hello> {

    public HelloMinervaRequest(HydraApplication app) {
        super(Hello.class, app);
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
        return 0;
    }
}
