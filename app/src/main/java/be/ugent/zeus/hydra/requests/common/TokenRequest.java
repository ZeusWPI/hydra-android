package be.ugent.zeus.hydra.requests.common;

import be.ugent.zeus.hydra.minerva.auth.requests.TokenRequestInterceptor;
import be.ugent.zeus.hydra.requests.exceptions.RestTemplateException;
import be.ugent.zeus.hydra.requests.exceptions.TokenException;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * A request that requires a token to be added to the request, for example an authentication token.
 *
 * @author Niko Strijbol
 */
public abstract class TokenRequest<T> extends JsonSpringRequest<T> {

    public TokenRequest(Class<T> clazz) {
        super(clazz);
    }

    /**
     * Get the token to use in the request.
     *
     * @return The token.
     */
    protected abstract String getToken() throws TokenException;

    /**
     * Set the API interceptor.
     */
    @Override
    protected RestTemplate createRestTemplate() throws RestTemplateException {
        try {
            RestTemplate t = super.createRestTemplate();
            t.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new TokenRequestInterceptor(getToken())));
            return t;
        } catch (TokenException e) {
            throw new RestTemplateException(e);
        }
    }
}