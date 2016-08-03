/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University Ghent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in all copies or
 *      substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package be.ugent.zeus.hydra.auth.requests;

import android.util.Log;

import be.ugent.zeus.hydra.auth.EndpointConfiguration;
import be.ugent.zeus.hydra.auth.OAuthConfiguration;
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
        Log.d(TAG, "Requesting access token, grant_type: authorization_code.");
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(EndpointConfiguration.TOKEN_ENDPOINT)
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