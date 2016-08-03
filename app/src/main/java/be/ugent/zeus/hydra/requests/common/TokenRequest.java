package be.ugent.zeus.hydra.requests.common;

import java.io.Serializable;
import java.util.Collections;

import be.ugent.zeus.hydra.auth.requests.TokenRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * A request that requires a token.
 *
 * @author Niko Strijbol
 */
public abstract class TokenRequest<T extends Serializable> extends AbstractRequest<T> {

    public TokenRequest(Class<T> clazz) {
        super(clazz);
    }

    /**
     * Get the token to use in the request.
     *
     * @return The token.
     */
    protected abstract String getToken();

    /**
     * Set the API interceptor.
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate t = super.createRestTemplate();
        t.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new TokenRequestInterceptor(getToken())));
        return t;
    }
}
