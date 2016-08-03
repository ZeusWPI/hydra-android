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

package be.ugent.zeus.hydra.auth;

import java.io.IOException;

import android.support.annotation.NonNull;

import be.ugent.android.sdk.oauth.OAuthConfiguration;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.requests.Request;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Exchange an authorisation code for an access code.
 */
public abstract class AccessTokenRequest implements Request<BearerToken> {

    protected OAuthConfiguration configData;
    protected String code;

    public AccessTokenRequest(OAuthConfiguration configData, String code) {
        this.configData = configData;
        this.code = code;
    }

    @NonNull
    @Override
    public BearerToken performRequest() throws RequestFailureException {
        OAuthJSONAccessTokenResponse accessTokenResponse;

        try {
            accessTokenResponse = getToken();
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(accessTokenResponse.getBody(), BearerToken.class);
        } catch (IOException | OAuthSystemException | OAuthProblemException e) {
            throw new RequestFailureException(e);
        }
    }

    /**
     * Get the token.
     *
     * @return The token data.
     *
     * @throws OAuthProblemException See documentation of {@link OAuthProblemException}.
     * @throws OAuthSystemException See documentation of {@link OAuthSystemException}.
     */
    protected abstract OAuthJSONAccessTokenResponse getToken() throws OAuthProblemException, OAuthSystemException;
}