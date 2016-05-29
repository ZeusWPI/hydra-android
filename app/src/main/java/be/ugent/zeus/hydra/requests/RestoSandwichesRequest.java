package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.resto.Sandwiches;

/**
 * Request the list of sandwiches.
 *
 * @author feliciaan
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
        return ZEUS_API_URL + "2.0/resto/sandwiches.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_HOUR * 12;
    }
}
