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

package be.ugent.android.sdk.oauth.request;

import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.android.sdk.oauth.EndpointConfiguration;
import be.ugent.android.sdk.oauth.OAuthConfiguration;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.android.sdk.oauth.storage.TokenData;
import be.ugent.zeus.hydra.loader.NetworkRequest;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


public class BearerTokenRequest implements NetworkRequest<BearerToken> {

    private static final String TAG = "BearerTokenRequest";

    private OAuthConfiguration configData;
    private String code;
    private TokenData oldToken;

    public BearerTokenRequest(OAuthConfiguration configData, String code) {
        this.configData = configData;
        this.code = code;
    }

    public BearerTokenRequest(OAuthConfiguration configData, TokenData oldToken) {
        this.oldToken = oldToken;
        this.configData = configData;
    }

    @NonNull
    @Override
    public BearerToken performRequest() throws RequestFailureException {
        OAuthJSONAccessTokenResponse accessTokenResponse;

        try {
            if (oldToken != null) {
                accessTokenResponse = refreshAccessToken();
            } else {
                accessTokenResponse = requestAccessToken();
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(accessTokenResponse.getBody(), BearerToken.class);
        } catch (IOException | OAuthSystemException | OAuthProblemException e) {
            throw new RequestFailureException(e);
        }
    }

    /**
     * Performs a token request based on the token refresh grant.
     *
     * @return The response of the token request.
     * @throws org.apache.oltu.oauth2.common.exception.OAuthProblemException
     * @throws org.apache.oltu.oauth2.common.exception.OAuthSystemException
     */
    private OAuthJSONAccessTokenResponse refreshAccessToken()
            throws OAuthProblemException, OAuthSystemException {
        Log.d(TAG, "Refreshing access token, grant_type: refresh_token.");
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(EndpointConfiguration.TOKEN_ENDPOINT)
                .setGrantType(GrantType.REFRESH_TOKEN)
                .setClientId(configData.API_KEY)
                .setClientSecret(configData.API_SECRET)
                .setRefreshToken(oldToken.getRefreshToken())
                .setRedirectURI(configData.CALLBACK_URI)
                .buildBodyMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        return oAuthClient.accessToken(request, OAuth.HttpMethod.POST);
    }


    /**
     * Performs a token request based on the authorization grant.
     *
     * @return The response of the token request.
     * @throws org.apache.oltu.oauth2.common.exception.OAuthSystemException
     * @throws org.apache.oltu.oauth2.common.exception.OAuthProblemException
     */
    private OAuthJSONAccessTokenResponse requestAccessToken() throws OAuthSystemException, OAuthProblemException {
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