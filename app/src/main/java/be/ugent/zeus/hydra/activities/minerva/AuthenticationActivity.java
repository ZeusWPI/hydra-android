package be.ugent.zeus.hydra.activities.minerva;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.widget.Toast;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import be.ugent.android.sdk.oauth.EndpointConfiguration;
import be.ugent.android.sdk.oauth.event.AuthorizationCodeEventHandler;
import be.ugent.android.sdk.oauth.event.AuthorizationEvent;
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
public class AuthenticationActivity extends ToolbarActivity implements AuthorizationCodeEventHandler {

    private static final String TAG = "AuthenticationActivity";

    private AuthorizationManager authorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        authorizationManager = ((HydraApplication) getApplication()).getAuthorizationManager();

        //Show warning if it is not a Ugent network.
        if(!NetworkUtils.isUgentNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.auth_maybe_not_ugent, Toast.LENGTH_LONG).show();
        }

        //Show login screen.
        if (authorizationManager.isAuthenticated()) {
            finishWithResult();
        } else {
            NetworkUtils.launchCustomTabOrBrowser(authorizationManager.getRequestUri(), this);
        }
    }

    /**
     * Handle new intents. If the intent contains our code, we have a winner!
     *
     * @param intent The intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();

        Log.d(TAG, "Uri is: " + uri);

        // Handle the callback URI.
        if(uri.getScheme().equals(EndpointConfiguration.CALLBACK_SCHEME)) {

            String errorParameter = uri.getQueryParameter("error");

            // Successful authorization: send the code and event.
            if (errorParameter == null) {
                receiveAuthorizationCode(uri.getQueryParameter("code"));
            }
            // failed authorization
            else {
                Log.e(TAG, "Authorization failed: " + uri.getQueryParameter("error_description"));
                onAuthorizationEvent(AuthorizationEvent.AUTHENTICATION_FAILED);
            }
        }
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
        NavUtils.navigateUpFromSameTask(this);
        //finish();
    }
}