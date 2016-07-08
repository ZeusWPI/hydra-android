package be.ugent.zeus.hydra.requests.minerva;

import be.ugent.android.sdk.oauth.EndpointConfiguration;
import be.ugent.android.sdk.oauth.json.GrantInformation;
import be.ugent.zeus.hydra.HydraApplication;

import static be.ugent.zeus.hydra.loader.cache.Cache.ONE_HOUR;

/**
 * Created by feliciaan on 21/06/16.
 */
public class UserInfoRequest extends MinervaRequest<GrantInformation> {

    public UserInfoRequest(HydraApplication app) {
        super(GrantInformation.class, app);
    }

    @Override
    public String getCacheKey() {
        return "cas.grantInfo";
    }

    @Override
    protected String getAPIUrl() {
        return EndpointConfiguration.GRANT_INFORMATION_ENDPOINT;
    }

    @Override
    public long getCacheDuration() {
        return ONE_HOUR * 2;
    }
}
