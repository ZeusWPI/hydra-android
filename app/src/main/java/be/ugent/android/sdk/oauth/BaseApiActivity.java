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

package be.ugent.android.sdk.oauth;

import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;

import be.ugent.android.sdk.UGentApplication;
import be.ugent.android.sdk.oauth.event.AuthorizationEvent;
import be.ugent.android.sdk.oauth.event.AuthorizationEventListener;
import be.ugent.android.sdk.oauth.exception.OAuthException;
import be.ugent.android.sdk.oauth.exception.OAuthTokenException;
import be.ugent.android.sdk.oauth.json.BearerToken;
import roboguice.activity.RoboActivity;


public abstract class BaseApiActivity extends RoboActivity implements AuthorizationEventListener {

    private static final String TAG = "BaseApiActivity";
    protected SpiceManager spiceManager = new SpiceManager(UGentSpiceService.class);

    @Inject private AuthorizationManager authorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure AuthMgr with the application's OAuthConfiguration.
        UGentApplication applicationCtx = (UGentApplication) getApplicationContext();
        OAuthConfiguration configData = applicationCtx.getOAuthConfiguration();
        authorizationManager.setOAuthConfiguration(configData);
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    /**
     * Performs a SpiceRequest after ensuring a valid access token is present.
     *
     * @param request
     * @param requestCacheKey
     * @param cacheExpiryDuration
     * @param requestListener
     * @param <T>
     */
    protected <T> void executeRequest(
            final SpiceRequest<T> request,
            final Object requestCacheKey,
            final long cacheExpiryDuration,
            final RequestListener<T> requestListener) {

        /**
         * Dynamically built request listener that will handle the refreshing
         * of the access tokens. When the token has become invalid, it will first
         * execute a refresh token request and perform the original request only
         * after a successful retrieval of a new token.
         *
         * On Failure:
         *  - Figure out what went wrong with the Bearer token request.
         *  - Notify the original request listener of the request failure
         *
         * On Success:
         *  - Save the newly received token.
         *  - Perform the original request with the original request listener.
         */
        class BearerTokenRequestListener implements RequestListener<BearerToken> {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, "Failed retrieval of next token.");
                // handle change of client credentials
                try {
                    OAuthProblemException oAuthProblemException =
                            (OAuthProblemException) spiceException.getCause();
                    if (oAuthProblemException.getError().equals("invalid_client")) {
                        requestListener.onRequestFailure(new OAuthTokenException(
                                OAuthException.ERROR.CREDENTIALS_CHANGED
                        ));
                        return;
                    }
                } catch (Exception e) {
                    // Was it something else? Log it.
                    e.printStackTrace();
                }

                // default error to unknown
                requestListener.onRequestFailure(new OAuthTokenException(
                        OAuthException.ERROR.UNKNOWN_ERROR
                ));
            }

            @Override
            public void onRequestSuccess(BearerToken bearerToken) {
                authorizationManager.setBearerToken(bearerToken);
                Log.d(TAG, "Executing user request.");
                spiceManager.execute(request,
                        requestCacheKey,
                        cacheExpiryDuration,
                        requestListener);
            }
        }

        if (!authorizationManager.hasValidToken()) {
            // perform a bearer token request prior to performing the initial request.
            spiceManager.execute(
                    authorizationManager.buildTokenRefreshRequest(),
                    null,
                    DurationInMillis.ONE_HOUR,
                    new BearerTokenRequestListener());
        } else {
            // perform the initial request
            spiceManager.execute(request,
                        requestCacheKey,
                        cacheExpiryDuration,
                        requestListener);
        }

    }

    public void requestFirstAccessToken(String authorizationCode) {
        spiceManager.execute(
                authorizationManager.buildGrantTokenRequest(authorizationCode),
                null,
                DurationInMillis.ONE_HOUR,
                new InitialBearerTokenRequestListener());
    }

    class InitialBearerTokenRequestListener implements RequestListener<BearerToken> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, "Failed retrieval of next token.");
            // TODO: report error and sign out user.
        }

        @Override
        public void onRequestSuccess(BearerToken bearerToken) {
            authorizationManager.setBearerToken(bearerToken);
            onAuthorizationEventReceived(AuthorizationEvent.AUTHENTICATION_SUCCESS);
        }
    }


    @Override
    public void onAuthorizationEventReceived(AuthorizationEvent event) {}

}
