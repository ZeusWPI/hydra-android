package be.ugent.zeus.hydra.requests.minerva;

import java.util.Collections;

import be.ugent.android.sdk.oauth.EndpointConfiguration;
import be.ugent.android.sdk.oauth.json.GrantInformation;
import be.ugent.android.sdk.oauth.request.ManualAccessTokenRequestInterceptor;
import be.ugent.zeus.hydra.requests.AbstractRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import static be.ugent.zeus.hydra.loader.cache.Cache.ONE_HOUR;

/**
 * Created by feliciaan on 21/06/16.
 */
public class SimpleUserInfoRequest extends AbstractRequest<GrantInformation> {

    private String token;

    public SimpleUserInfoRequest(String token) {
        super(GrantInformation.class);
        this.token = token;
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

    /**
     * Set the API interceptor.
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate t = super.createRestTemplate();
        t.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new ManualAccessTokenRequestInterceptor(token)));
        return t;
    }
}
