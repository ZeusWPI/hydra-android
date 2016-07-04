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

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import be.ugent.android.sdk.oauth.event.AuthorizationEvent;
import be.ugent.android.sdk.oauth.event.AuthorizationEventListener;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.android.sdk.oauth.request.BearerTokenRequest;
import be.ugent.android.sdk.oauth.storage.StorageManager;
import be.ugent.android.sdk.oauth.storage.TokenData;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.loader.NetworkRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

import java.util.Calendar;

public class AuthorizationManager {

    public static final String TAG = "UG_AuthorizationManager";
    private OAuthConfiguration configData;
    private StorageManager storageManager;
    private TokenData currentToken;

    public AuthorizationManager(Context context) {
        this.storageManager = new StorageManager(context);
        this.configData = new OAuthConfiguration.Builder()
                .apiKey(BuildConfig.OAUTH_ID)
                .apiSecret(BuildConfig.OAUTH_SECRET)
                .callbackUri("https://zeus.ugent.be/hydra/oauth/callback")
                .build();
    }

    /**
     * Configures the Authorization manager.
     *
     * @param configData Application specific configuration data.
     */
    public void setOAuthConfiguration(OAuthConfiguration configData) {
        this.configData = configData;
    }


    /**
     * Checks if the user is authenticated, i.e. he has an
     * authentication grant and a means to request a new token
     * if necessary.
     *
     * The following strategy is used to assert a valid authentication. Authentication
     * is asserted when any of the following steps returns a positive result:
     *
     *  1. Retrieve saved token from local persistence is valid
     *  2. Refreshed token
     *
     * @return True if a user is authenticated with OAuth.
     */
    public boolean isAuthenticated() {
        currentToken = storageManager.loadToken();
        return hasValidToken();
    }


    /**
     * Loads authorization page of the provider in the WebView.
     *
     * @param webView The WebView which will render the authorization page.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void showAuthorizationPage(final AuthorizationEventListener ctx, final WebView webView) {
        // activate javascript support
        Log.d(TAG, "Enabling Javascript support on webview");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // callback listener for successful redirection
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "Received Url Redirect Callback: " + url);

                // Received callback from the Authentication Server
                if (url.startsWith(configData.CALLBACK_URI)) {
                    Uri uri = Uri.parse(url);

                    // successful authorization
                    String errorParameter = uri.getQueryParameter("error");
                    if (errorParameter == null) {
                        // set authorization code
                        BaseApiActivity activity = (BaseApiActivity) ctx;
                        activity.requestFirstAccessToken(uri.getQueryParameter("code"));
                    }

                    // failed authorization
                    else {
                        String errorMessage = uri.getQueryParameter("error_description");
                        ctx.onAuthorizationEventReceived(AuthorizationEvent.AUTHENTICATION_FAILED);
                    }

                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        });

        // Direct webview to Authorization URL.
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation(EndpointConfiguration.AUTHORIZATION_ENDPOINT)
                    .setResponseType(ResponseType.CODE.toString())
                    .setClientId(configData.API_KEY)
                    .setRedirectURI(configData.CALLBACK_URI)
                    .setState("auth_state")
                    .buildQueryMessage();
            webView.loadUrl(request.getLocationUri());
        } catch (OAuthSystemException e) {
            Log.e(TAG, "Error while building URI", e);
            e.printStackTrace();
            // TODO: what if we can't build the URI?
        }
    }


    /**
     * Builds a token request based on the grant information.
     *
     * @return BearerTokenRequest based on an authorization code.
     */
    public NetworkRequest<BearerToken> buildGrantTokenRequest(String authorizationCode) {
        return new BearerTokenRequest(configData, authorizationCode);
    }

    /**
     * Builds a token refresh request based on the current token.
     *
     * @return A request to fetch the next bearer token using the refresh token.
     */
    public NetworkRequest<BearerToken> buildTokenRefreshRequest() {
        return new BearerTokenRequest(configData, currentToken);
    }

    /**
     * Checks if the currentToken is still within a valid time range.
     *
     * @return True if token is still valid
     */
    public boolean hasValidToken() {
        if (currentToken == null) {
            return false;
        }
        Calendar rightNow = Calendar.getInstance();
        return rightNow.compareTo(currentToken.getValidUntil()) < 0;
    }

    /**
     * Receives the fresh bearer token and sets it as current.
     *
     * @param bearerToken New bearer token received from auth server.
     */
    public void setBearerToken(BearerToken bearerToken) {
       currentToken = storageManager.saveToken(bearerToken);
    }

    /**
     * Returns the access token string of the latest token.
     *
     * @return The current access token.
     */
    public String getAccessToken() {
        if (currentToken != null) {
            return currentToken.getAccessToken();
        }
        return null;
    }

    /**
     * Returns the currently stored token data.
     *
     * @return The current token being used by Api requests.
     */
    public TokenData getCurrentToken() {
        return currentToken;
    }


    /**
     * Removes the current token from the authorization manager.
     */
    public void clearToken() {
        currentToken = null;
    }


    /**
    * Signs out the user from the service. This will remove all previously
    * stored authorization data, such as tokens, etc.
    */
    public void signOut() {
        storageManager.removeToken();
        clearToken();
    }

    /**
     * Signs out the user from the service. This will remove all previously
     * stored authorization data, such as tokens, etc.
     * Will notify event listener of this action.
     *
     * @param listener The event listener to be informed of the signout.
     */
    public void signOut(AuthorizationEventListener listener) {
        signOut();
        listener.onAuthorizationEventReceived(AuthorizationEvent.SIGNED_OUT);
    }

    //
    // For Testing Purpose Only.
    //

    /**
     * Invalidates the token by
     */
    public void invalidateToken() {
        Calendar validUntil = Calendar.getInstance();
        validUntil.add(Calendar.HOUR, -2);

        currentToken = new TokenData(
            currentToken.getAccessToken(),
            currentToken.getRefreshToken(),
            currentToken.getTokenType(),
            currentToken.getCreatedOn(),
            validUntil);
        Log.i(TAG, "Token invalidated.");
    }
}
