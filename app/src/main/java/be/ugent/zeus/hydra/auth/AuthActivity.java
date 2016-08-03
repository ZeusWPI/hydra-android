package be.ugent.zeus.hydra.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import be.ugent.android.sdk.oauth.EndpointConfiguration;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.android.sdk.oauth.json.GrantInformation;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.requests.Request;
import be.ugent.zeus.hydra.requests.minerva.SimpleUserInfoRequest;
import be.ugent.zeus.hydra.utils.NetworkUtils;

/**
 * An activity to prompt the user to authorise our access to the account.
 *
 * @author Niko Strijbol
 */
public class AuthActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "AuthActivity";

    //The account type we want.
    public static final String ARG_ACCOUNT_TYPE = "accountType";
    //The auth type or scope.
    public static final String ARG_AUTH_TYPE = "authType";
    //Adding a new account or not?
    public static final String ARG_ADDING_NEW_ACCOUNT = "addingNewAccount";

    private String accountType;
    private String authType;

    private AccountHelper helper = new AccountHelper();
    private AccountManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Bundle bundle = getIntent().getExtras();

        accountType = bundle.getString(ARG_ACCOUNT_TYPE, EndpointConfiguration.ACCOUNT_TYPE);
        authType = bundle.getString(ARG_AUTH_TYPE, EndpointConfiguration.DEFAULT_SCOPE);

        manager = AccountManager.get(this);

        //Launch custom tab
        NetworkUtils.launchCustomTabOrBrowser(helper.getRequestUri(), this);
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
                InfoTask task = new InfoTask();
                task.execute(uri.getQueryParameter("code"));
            }
            // failed authorization
            else {
                Log.e(TAG, "Authorization failed: " + uri.getQueryParameter("error_description"));
                finishWithError();
            }
        }
    }

    private void finishOK(Intent intent) {
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void finishWithError() {
        setAccountAuthenticatorResult(null);
        finish();
    }

    private class InfoTask extends AsyncTask<String, Void, Intent> {

        @Override
        protected Intent doInBackground(String... strings) {

            String token = strings[0];

            //First we get an auth code.
            try {
                Request<BearerToken> request = helper.buildAuthTokenRequest(token);
                BearerToken result = request.performRequest();

                //Get the information
                SimpleUserInfoRequest infoRequest = new SimpleUserInfoRequest(result.accessToken);
                GrantInformation information = infoRequest.performRequest();

                //Account name
                String name;
                if(information.userAttributes.email.size() == 0) {
                    name = "Minerva-account";
                } else {
                    name = information.userAttributes.email.get(0);
                }

                //Save the account.
                Account account = new Account(name, accountType);
                if(getIntent().getBooleanExtra(ARG_ADDING_NEW_ACCOUNT, false)) {
                    manager.addAccountExplicitly(account, result.refreshToken, null);
                } else {
                    manager.setPassword(account, result.refreshToken);
                }

                manager.setUserData(account, MinervaAuthenticator.EXPIRATION_DATE, String.valueOf(result.expiresIn));
                manager.setAuthToken(account, authType, result.accessToken);

                //Make intent for return value
                Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, name);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, result.accessToken);

                return res;
            } catch (RequestFailureException e) {
                Log.w(TAG, "First request for account failed.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Intent intent) {
            if(intent == null) {
                finishWithError();
            } else {
                finishOK(intent);
            }
        }
    }
}