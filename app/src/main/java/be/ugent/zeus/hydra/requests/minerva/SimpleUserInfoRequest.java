package be.ugent.zeus.hydra.requests.minerva;

import java.util.Collections;

import android.support.annotation.NonNull;

import be.ugent.android.sdk.oauth.EndpointConfiguration;
import be.ugent.android.sdk.oauth.json.GrantInformation;
import be.ugent.android.sdk.oauth.request.ManualAccessTokenRequestInterceptor;
import be.ugent.zeus.hydra.requests.AbstractRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import static be.ugent.zeus.hydra.loader.cache.Cache.ONE_HOUR;

/**
 * This is the user information request. This is a special request that needs a token, since this request is part of
 * the account creation process and is called before the account is saved on the device.
 *
 * TODO: this is not a cacheable request, but it currently does inherit the interface!
 */
public class SimpleUserInfoRequest extends AbstractRequest<GrantInformation> {

    private String token;

    public SimpleUserInfoRequest(String token) {
        super(GrantInformation.class);
        this.token = token;
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "cas.grantInfo";
    }

    @NonNull
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