package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import be.ugent.android.sdk.oauth.BaseApiActivity;
import be.ugent.android.sdk.oauth.event.AuthorizationEvent;
import be.ugent.zeus.hydra.R;
import roboguice.inject.InjectView;

public class AuthenticationActivity extends BaseApiActivity {

    @InjectView(R.id.oauthWebView) private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Immediately redirect the user to the `StatusActivity`
        // if he is already signed in.
        if (getAuthorizationManager().isAuthenticated()) {
            finish();
        } else {
            // This will tell the `AuthorizationManager` to start an oAuth 2.0
            // authorization flow within this particular `WebView`.
            getAuthorizationManager().showAuthorizationPage(this, webView);
        }
    }

    @Override
    public void onAuthorizationEventReceived(AuthorizationEvent event) {
        Intent intent;
        switch (event) {
            // Authorization successful, lets redirect the user
            // to our status activity.
            case AUTHENTICATION_SUCCESS:
                //intent = new Intent(this, StatusActivity.class);
                //startActivity(intent);
                System.out.println("Authentication success");
                finish();
                break;

            // Pity, the user didn't allow our application access to
            // his personal information.
            case AUTHENTICATION_FAILED:
                System.out.println("User doesn't trust us with his data! :-(");
        }
    }
}
