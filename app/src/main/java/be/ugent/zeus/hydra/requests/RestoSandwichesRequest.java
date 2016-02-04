package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.Resto.Sandwiches;

/**
 * Created by feliciaan on 04/02/16.
 */
public class RestoSandwichesRequest extends AbstractRequest<Sandwiches> {

    public RestoSandwichesRequest() {
        super(Sandwiches.class);
    }

    @Override
    public String getCacheKey() {
        return "sandwiches.json";
    }

    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "resto/1.0/sandwiches.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_HOUR * 6;
    }
}
