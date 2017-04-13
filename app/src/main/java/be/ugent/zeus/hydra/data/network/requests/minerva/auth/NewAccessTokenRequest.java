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
 * Exchange an authorisation code for an access code. This is called the first time after an account has been added and
 * the user has granted access. After this, the refresh token is used to get new access codes until the account becomes
 * invalid.
 *
 * @author Niko Strijbol
 */
public class NewAccessTokenRequest extends AccessTokenRequest {

    private static final String TAG = "NewAccessTokenRequest";

    public NewAccessTokenRequest(OAuthConfiguration configData, String code) {
        super(configData, code);
    }

    /**
     * Get the token. This exchanges the authorisation code.
     *
     * @return The token data.
     *
     * @throws OAuthProblemException See documentation of {@link OAuthProblemException}.
     * @throws OAuthSystemException  See documentation of {@link OAuthSystemException}.
     */
    @Override
    protected OAuthJSONAccessTokenResponse getToken() throws OAuthProblemException, OAuthSystemException {
        Log.d(TAG, "Requesting access token based on the authorisation code.");
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(MinervaConfig.TOKEN_ENDPOINT)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(configData.API_KEY)
                .setClientSecret(configData.API_SECRET)
                .setCode(code)
                .setRedirectURI(configData.CALLBACK_URI)
                .buildBodyMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        return oAuthClient.accessToken(request, OAuth.HttpMethod.POST);
    }
}