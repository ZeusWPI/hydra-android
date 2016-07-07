package be.ugent.zeus.hydra.activities.minerva;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import be.ugent.android.sdk.oauth.event.AuthorizationEvent;
import be.ugent.android.sdk.oauth.event.AuthorizationEventHandler;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.requests.RequestExecutor;
import be.ugent.zeus.hydra.utils.NetworkUtils;

/**
 * Activity that shows a WebView to maybe authenticate with Minerva. This activity should probably be used as
 * an activity with a result.
 */
public class AuthenticationActivity extends ToolbarActivity implements AuthorizationEventHandler {

    private static final String TAG = "AuthenticationActivity";

    private AuthorizationManager authorizationManager;

    private WebView webView;
    private ProgressBar progressBar;
    private TextView needsUgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        webView = $(R.id.oauthWebView);
        progressBar = $(R.id.progress_bar);
        needsUgent = $(R.id.auth_needs_ugent);

        authorizationManager = ((HydraApplication) getApplication()).getAuthorizationManager();

        //Show warning if it is not a Ugent network.
        if(!NetworkUtils.isUgentNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.auth_maybe_not_ugent, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Starting onResume.");
        // Immediately redirect the user to the if he is already signed in.
        if (authorizationManager.isAuthenticated()) {
            finishWithResult();
        } else {
            // This will tell the `AuthorizationManager` to start an oAuth 2.0
            // authorization flow within this particular `WebView`.
            authorizationManager.showAuthorizationPage(this, new WebViewListener() {
                @Override
                public void onPageFinished(String url) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onPageStarted(String url, Bitmap favicon) {
                    webView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(int code, String description) {
                    Log.e(TAG, "HTTP error occured, #" + code + ": " + description);
                    switch (code) {
                        case WebViewClient.ERROR_TIMEOUT:
                            webView.stopLoading();
                            webView.setVisibility(View.GONE);
                            needsUgent.setVisibility(View.VISIBLE);
                            break;
                        case WebViewClient.ERROR_CONNECT:
                            onAuthorizationEvent(AuthorizationEvent.NO_NETWORK);
                            break;
                        default:
                            //Do nothing.
                    }
                }
            });
        }
        Log.d(TAG, "OnResume done.");
    }

    /**
     * @return The WebView that will be used to show the authorize screen.
     */
    @NonNull
    @Override
    public WebView getWebView() {
        return webView;
    }

    /**
     * Receive authorization events.
     *
     * @param event The event.
     */
    @Override
    public void onAuthorizationEvent(AuthorizationEvent event) {
        switch (event) {
            // Authorization successful, lets redirect the user
            // to our status activity.
            case AUTHENTICATION_SUCCESS:
                System.out.println("Authentication success");
                finishWithResult();
                break;
            case AUTHENTICATION_FAILED:
                //When this happens, we display a toast informing the user and close this activity.
                //The activity is closed without result.
                Log.e(TAG, "The user denied the authorization.");
                Toast.makeText(getApplicationContext(), R.string.auth_denied, Toast.LENGTH_LONG).show();
                finish();
                break;
            case NO_NETWORK:
                Log.e(TAG, "No network.");
                Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    /**
     * If the authorization was successful, an authorization code is given. With this code you can request a
     * {@link be.ugent.android.sdk.oauth.json.BearerToken}.
     *
     * @param authorizationCode The authorization code.
     */
    @Override
    public void receiveAuthorizationCode(String authorizationCode) {
        RequestExecutor.executeAsync(authorizationManager.buildGrantTokenRequest(authorizationCode), new RequestExecutor.Callback<BearerToken>() {
            @Override
            public void receiveData(@NonNull BearerToken data) {
                //Set the token.
                authorizationManager.setBearerToken(data);
                onAuthorizationEvent(AuthorizationEvent.AUTHENTICATION_SUCCESS);
            }

            @Override
            public void receiveError(RequestFailureException e) {
                Log.e(TAG, "Failed retrieval of next token.");
                Log.e(TAG, e.getLocalizedMessage());
            }
        });
    }

    /**
     * Set the result and finish.
     */
    private void finishWithResult() {
        setResult(RESULT_OK);
        finish();
    }
}