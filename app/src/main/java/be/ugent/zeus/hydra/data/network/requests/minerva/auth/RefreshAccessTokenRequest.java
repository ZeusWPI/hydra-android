package be.ugent.zeus.hydra.data.network.requests.minerva.auth;

import android.util.Log;

import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.auth.OAuthConfiguration;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

/**
 * Exchange a refresh code for an access code.
 *
 * @author Niko Strijbol
 */
public class RefreshAccessTokenRequest extends AccessTokenRequest {

    private static final String TAG = "RefreshAccessTokenReq";

    public RefreshAccessTokenRequest(OAuthConfiguration configData, String code) {
        super(configData, code);
    }

    /**
     * Performs a token request based on the token refresh grant.
     *
     * @return The response of the token request.
     *
     * @throws OAuthProblemException See documentation of {@link OAuthProblemException}.
     * @throws OAuthSystemException  See documentation of {@link OAuthSystemException}.
     */
    protected OAuthJSONAccessTokenResponse getToken() throws OAuthProblemException, OAuthSystemException {
        Log.d(TAG, "Requesting new access code based on the refresh token.");
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(MinervaConfig.TOKEN_ENDPOINT)
                .setGrantType(GrantType.REFRESH_TOKEN)
                .setClientId(configData.API_KEY)
                .setClientSecret(configData.API_SECRET)
                .setRefreshToken(code)
                .setRedirectURI(configData.CALLBACK_URI)
                .buildBodyMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        return oAuthClient.accessToken(request, OAuth.HttpMethod.POST);
    }
}