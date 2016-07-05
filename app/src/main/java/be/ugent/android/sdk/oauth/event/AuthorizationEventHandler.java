package be.ugent.android.sdk.oauth.event;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.webkit.WebView;

/**
 * Interface for the object that handles the visual part of logging in.
 * This is often an {@link android.app.Activity} or {@link android.app.Fragment}, but it really doesn't matter
 * what it is.
 *
 * @author Niko Strijbol
 */
public interface AuthorizationEventHandler {

    /**
     * @return The WebView that will be used to show the authorize screen.
     */
    @NonNull
    WebView getWebView();

    /**
     * Receive authorization events.
     *
     * Note: the success event is not fired by the {@link be.ugent.android.sdk.oauth.AuthorizationManager}. This should
     * be done by the client class inside the {@link #receiveAuthorizationCode(String)} method.
     *
     * @param event The event.
     */
    void onAuthorizationEvent(AuthorizationEvent event);

    /**
     * If the authorization was successful, an authorization code is given. With this code you can request a
     * {@link be.ugent.android.sdk.oauth.json.BearerToken}.
     *
     * @param authorizationCode The authorization code.
     */
    void receiveAuthorizationCode(String authorizationCode);

    /**
     * Allows for basic listening to the WebView.
     *
     * Note: once API 23 and 24 are the minimum version, the signature of this interface will change to use
     * the new methods.
     */
    interface WebViewListener {

        /**
         * Is called when {@link android.webkit.WebViewClient#onPageFinished(WebView, String)} is called.
         *
         * @param url The url of the page.
         */
        void onPageFinished(String url);

        /**
         * Is called when {@link android.webkit.WebViewClient#onPageStarted(WebView, String, Bitmap)} is called.
         *
         * @param url The url to be loaded.
         * @param favicon The favicon for this page if it already exists in the database.
         */
        void onPageStarted(String url, Bitmap favicon);

        /**
         * Is called when {@link android.webkit.WebViewClient#onReceivedError(WebView, int, String, String)}
         * is called.
         *
         * @param code The error code corresponding to an ERROR_* value.
         * @param description A String describing the error.
         */
        void onError(int code, String description);
    }
}