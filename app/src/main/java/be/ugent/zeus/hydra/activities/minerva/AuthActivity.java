package be.ugent.zeus.hydra.activities.minerva;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarAccountAuthenticatorActivity;
import be.ugent.zeus.hydra.auth.AccountUtils;
import be.ugent.zeus.hydra.auth.MinervaConfig;
import be.ugent.zeus.hydra.auth.MinervaAuthenticator;
import be.ugent.zeus.hydra.auth.models.BearerToken;
import be.ugent.zeus.hydra.auth.models.GrantInformation;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.minerva.UserInfoRequest;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.utils.customtabs.CustomTabsHelper;
import org.joda.time.DateTime;

/**
 * An activity to prompt the user to authorise our access to the account.
 *
 * @author Niko Strijbol
 */
public class AuthActivity extends ToolbarAccountAuthenticatorActivity implements ActivityHelper.ConnectionCallback {

    private static final String TAG = "AuthActivity";

    //The account type we want.
    public static final String ARG_ACCOUNT_TYPE = "accountType";
    //The auth type or scope.
    public static final String ARG_AUTH_TYPE = "authType";
    //Adding a new account or not?
    public static final String ARG_ADDING_NEW_ACCOUNT = "addingNewAccount";

    private String accountType;
    private String authType;

    private AccountManager manager;
    private ActivityHelper customTabActivityHelper;

    private TextView progressMessage;
    private boolean launched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        progressMessage = $(R.id.progress_message);

        Bundle bundle = getIntent().getExtras();

        accountType = bundle.getString(ARG_ACCOUNT_TYPE, MinervaConfig.ACCOUNT_TYPE);
        authType = bundle.getString(ARG_AUTH_TYPE, MinervaConfig.DEFAULT_SCOPE);

        manager = AccountManager.get(this);

        //Launch custom tab
        progressMessage.setText(getString(R.string.auth_progress_prepare));
        customTabActivityHelper = CustomTabsHelper.initHelper(this, this);
        customTabActivityHelper.setIntentFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        customTabActivityHelper.mayLaunchUrl(Uri.parse(AccountUtils.getRequestUri()), null, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
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
        if(uri.getScheme().equals(MinervaConfig.CALLBACK_SCHEME)) {

            String errorParameter = uri.getQueryParameter("error");

            // Successful authorization: send the code and event.
            if (errorParameter == null) {
                InfoTask task = new InfoTask();
                progressMessage.setText(getString(R.string.auth_progress_information));
                task.execute(uri.getQueryParameter("code"));
            }
            // failed authorization
            else {
                Log.e(TAG, "Authorization failed: " + uri.getQueryParameter("error_description"));
                finishWithError();
            }
        } else {
            Log.i(TAG, "Authorisation cancelled.");
            finishWithError();
        }
    }

    private void finishOK(Intent intent) {
        progressMessage.setText(getString(R.string.auth_progress_done));
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finishUp();
    }

    private void finishWithError() {
        progressMessage.setText(getString(R.string.auth_progress_failure));
        setAccountAuthenticatorResult(null);
        finishUp();
    }

    @Override
    public void onCustomTabsConnected(ActivityHelper activityHelper) {
        progressMessage.setText(getString(R.string.auth_progress_permission));
        if(!launched) {
            if(!NetworkUtils.isUgentNetwork(this)) {
                Toast.makeText(getApplicationContext(), R.string.auth_maybe_not_ugent, Toast.LENGTH_LONG).show();
            }
            activityHelper.openCustomTab(Uri.parse(AccountUtils.getRequestUri()));
            launched = true;
        }
    }

    @Override
    public void onCustomTabsDisconnected(ActivityHelper helper) {
        Log.d(TAG, "The custom tab was disconnected.");
    }

    private class InfoTask extends AsyncTask<String, Void, Intent> {

        @Override
        protected Intent doInBackground(String... strings) {

            String token = strings[0];

            //First we get an auth code.
            try {
                Request<BearerToken> request = AccountUtils.buildAuthTokenRequest(token);
                BearerToken result = request.performRequest();

                //Get the information
                UserInfoRequest infoRequest = new UserInfoRequest(result.getAccessToken());
                GrantInformation information = infoRequest.performRequest();

                //Account name
                String name;
                if(information.getUserAttributes().getEmail().size() == 0) {
                    name = "Minerva-account";
                } else {
                    name = information.getUserAttributes().getEmail().get(0).toLowerCase();
                }

                //Save the account.
                Account account = new Account(name, accountType);
                if(getIntent().getBooleanExtra(ARG_ADDING_NEW_ACCOUNT, false)) {
                    manager.addAccountExplicitly(account, result.getRefreshToken(), null);
                } else {
                    manager.setPassword(account, result.getRefreshToken());
                }

                DateTime expiration = DateTime.now().plusSeconds(result.getExpiresIn());
                manager.setUserData(account, MinervaAuthenticator.EXP_DATE, MinervaAuthenticator.formatter.print(expiration));
                manager.setAuthToken(account, authType, result.getAccessToken());

                //Make intent for return value
                Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, name);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, result.getAccessToken());

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