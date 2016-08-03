package be.ugent.zeus.hydra.auth;

import android.util.Log;

import be.ugent.android.sdk.oauth.EndpointConfiguration;
import be.ugent.android.sdk.oauth.OAuthConfiguration;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.loader.requests.Request;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

/**
 * @author Niko Strijbol
 * @version 3/08/2016
 */
public class AccountHelper {

    private OAuthConfiguration config;

    public AccountHelper() {
        this.config = new OAuthConfiguration.Builder()
                .apiKey(BuildConfig.OAUTH_ID)
                .apiSecret(BuildConfig.OAUTH_SECRET)
                .callbackUri("https://zeus.ugent.be/hydra/oauth/callback")
                .build();
    }

    /**
     * Builds a token request based on the grant information.
     *
     * @return BearerTokenRequest based on an authorization code.
     */
    public Request<BearerToken> buildAuthTokenRequest(String authorizationCode) {
        return new NewAccessTokenRequest(config, authorizationCode);
    }

    /**
     * Builds a token request based on the grant information.
     *
     * @return BearerTokenRequest based on an authorization code.
     */
    public Request<BearerToken> buildRefreshTokenRequest(String authorizationCode) {
        return new RefreshAccessTokenRequest(config, authorizationCode);
    }

    /**
     * @return The URI to be displayed to use authorize use.
     */
    public String getRequestUri() {
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation(EndpointConfiguration.AUTHORIZATION_ENDPOINT)
                    .setResponseType(ResponseType.CODE.toString())
                    .setClientId(config.API_KEY)
                    .setRedirectURI(config.CALLBACK_URI)
                    .setState("auth_state")
                    .buildQueryMessage();
            return request.getLocationUri();
        } catch (OAuthSystemException e) {
            Log.e("AccountHelper", "Error while building URI", e);
            //This shouldn't happen, so we intentionally crash the app.
            throw new IllegalStateException("This must not happen!", e);
        }
    }
}