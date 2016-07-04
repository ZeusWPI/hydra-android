package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.android.sdk.oauth.request.AccessTokenRequestInterceptor;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by feliciaan on 20/06/16.
 */
public abstract class MinervaRequest<T extends Serializable> extends AbstractRequest<T>  {

    private static final String TAG = "MinervaRequest";

    protected static final String MINERVA_API = "https://minqas.ugent.be/api/rest/v2/";

    protected AuthorizationManager authorizationManager;

    public MinervaRequest(Class<T> clazz, HydraApplication app) {
        super(clazz);
        authorizationManager = app.getAuthorizationManager();
    }

    protected Map<String, String> getURLVariables() {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("access_token", authorizationManager.getAccessToken());
        return urlVariables;
    }

    /**
     * If there is no valid token, we first get a new one. Then we execute the current request.
     */
    @NonNull
    @Override
    public T performRequest() throws RequestFailureException {

        Log.d(TAG, "Minerva data requested.");

        if (!authorizationManager.isAuthenticated() || authorizationManager.getAccessToken() == null) {
            Log.d(TAG, "Invalid/missing token; requesting new one.");
            // perform a bearer token request prior to performing the initial request.
            BearerToken token = authorizationManager.buildTokenRefreshRequest().performRequest();
            authorizationManager.setBearerToken(token);
        }

        Log.d(TAG, "Executing user request.");
        return super.performRequest();
    }

    /**
     * Set the API interceptor.
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate t = super.createRestTemplate();
        t.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new AccessTokenRequestInterceptor(authorizationManager)));
        return t;
    }
}
