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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import be.ugent.android.sdk.oauth.event.AuthorizationEvent;
import be.ugent.android.sdk.oauth.event.AuthorizationEventListener;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.loader.NetworkRequest;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.tasks.NetworkTaskExecutor;

import static be.ugent.android.sdk.oauth.event.AuthorizationEvent.AUTHENTICATION_FAILED;


public abstract class BaseApiActivity extends AppCompatActivity implements AuthorizationEventListener {

    private static final String TAG = "BaseApiActivity";

    protected AuthorizationManager authorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authorizationManager = ((HydraApplication) getApplication()).getAuthorizationManager();
    }

    /**
     * Performs a SpiceRequest after ensuring a valid access token is present.
     *
     * @param request
     * @param requestListener
     * @param <T>
     */
    protected <T> void executeRequest(
            final NetworkRequest<T> request,
            final NetworkTaskExecutor.NetworkCallBack<T> requestListener) {

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
        class BearerTokenRequestListener implements NetworkTaskExecutor.NetworkCallBack<BearerToken> {

            @Override
            public void receiveData(@NonNull BearerToken data) {
                authorizationManager.setBearerToken(data);
                Log.d(TAG, "Executing user request.");
                NetworkTaskExecutor.executeAsync(request, requestListener);
            }

            @Override
            public void receiveError(RequestFailureException e) {
                onAuthorizationEventReceived(AUTHENTICATION_FAILED);
                //requestListener.receiveError(e);
            }
        }

        if (!authorizationManager.hasValidToken()) {
            // perform a bearer token request prior to performing the initial request.
            NetworkTaskExecutor.executeAsync(authorizationManager.buildTokenRefreshRequest(), new BearerTokenRequestListener());
        } else {
            // perform the initial request
            NetworkTaskExecutor.executeAsync(request, requestListener);
        }

    }

    public void requestFirstAccessToken(String authorizationCode) {
        NetworkTaskExecutor.executeAsync(authorizationManager.buildGrantTokenRequest(authorizationCode), new InitialBearerTokenRequestListener());
    }

    class InitialBearerTokenRequestListener implements NetworkTaskExecutor.NetworkCallBack<BearerToken>{

        @Override
        public void receiveData(@NonNull BearerToken data) {
            authorizationManager.setBearerToken(data);
            onAuthorizationEventReceived(AuthorizationEvent.AUTHENTICATION_SUCCESS);
        }

        @Override
        public void receiveError(RequestFailureException e) {
            Log.e(TAG, "Failed retrieval of next token.");
            Log.e(TAG, e.getLocalizedMessage());
            // TODO: report error and sign out user.
        }
    }

    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    @Override
    public void onAuthorizationEventReceived(AuthorizationEvent event) {}
}