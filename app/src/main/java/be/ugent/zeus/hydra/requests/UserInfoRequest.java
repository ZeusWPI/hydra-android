package be.ugent.zeus.hydra.requests;

import android.content.Context;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.android.sdk.oauth.EndpointConfiguration;
import be.ugent.android.sdk.oauth.json.GrantInformation;

/**
 * Created by feliciaan on 21/06/16.
 */
public class UserInfoRequest extends MinervaRequest<GrantInformation> {
    public UserInfoRequest(Context context) {
        super(GrantInformation.class, context);
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
        return DurationInMillis.ONE_HOUR*2;
    }
}
