package be.ugent.zeus.hydra.ui.minerva;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.data.auth.MinervaAuthenticator;
import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.domain.models.minerva.auth.BearerToken;
import be.ugent.zeus.hydra.domain.models.minerva.auth.GrantInformation;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import be.ugent.zeus.hydra.data.network.requests.minerva.UserInfoRequest;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.customtabs.CustomTabsHelper;
import org.threeten.bp.LocalDateTime;

/**
 * An activity to prompt the user to authorise our access to the account.
 *
 * TODO: clean up
 *
 * @author Niko Strijbol
 * @see <a href="https://github.com/android/platform_frameworks_base/blob/master/core/java/android/accounts/AccountAuthenticatorActivity.java">AOSP</a>
 */
public class AuthActivity extends BaseActivity implements ActivityHelper.ConnectionCallback {

    //Extra bundle
    public static final String ARG_EXTRA_BUNDLE = "extraBundle";
    //Bundle key to provide up navigation or just finish the activity?
    public static final String KEY_PROVIDE_UP_NAVIGATION = "provideUpNavigation";
    //The account type we want.
    public static final String ARG_ACCOUNT_TYPE = "accountType";
    //The auth type or scope.
    public static final String ARG_AUTH_TYPE = "authType";
    //Adding a new account or not?
    public static final String ARG_ADDING_NEW_ACCOUNT = "addingNewAccount";
    private static final String TAG = "AuthActivity";
    private AccountAuthenticatorResponse response = null;
    private Bundle resultBundle = null;
    private Bundle extra = null;
    private String accountType;
    private String authType;
    private AccountManager manager;
    private ActivityHelper customTabActivityHelper;
    private TextView progressMessage;
    private boolean launched = false;

    /**
     * Set the result that is to be sent as the result of the request that caused this Activity to be launched. If
     * result is null or this method is never called then the request will be canceled.
     *
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        resultBundle = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        response = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (response != null) {
            response.onRequestContinued();
        }

        if (getIntent().hasExtra(ARG_EXTRA_BUNDLE)) {
            extra = getIntent().getBundleExtra(ARG_EXTRA_BUNDLE);
        }

        setContentView(R.layout.activity_authentication);

        progressMessage = findViewById(R.id.progress_message);

        Bundle bundle = getIntent().getExtras();

        accountType = bundle.getString(ARG_ACCOUNT_TYPE, MinervaConfig.ACCOUNT_TYPE);
        authType = bundle.getString(ARG_AUTH_TYPE, MinervaConfig.DEFAULT_SCOPE);

        manager = AccountManager.get(this);

        //Launch custom tab
        progressMessage.setText(getString(R.string.auth_progress_prepare));
        customTabActivityHelper = CustomTabsHelper.initHelper(this, false, this);
        customTabActivityHelper.setIntentFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        customTabActivityHelper.mayLaunchUrl(Uri.parse(AccountUtils.getRequestUri()), null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        if (uri != null && MinervaConfig.CALLBACK_SCHEME.equals(uri.getScheme())) {

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
        if (!launched) {
            activityHelper.openCustomTab(Uri.parse(AccountUtils.getRequestUri()));
            launched = true;
        }
    }

    @Override
    public void onCustomTabsDisconnected(ActivityHelper helper) {
        Log.d(TAG, "The custom tab was disconnected.");
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    private void finishUp() {
        if (response != null) {
            // send the result bundle back if set, otherwise send an error.
            if (resultBundle != null) {
                response.onResult(resultBundle);
            } else {
                response.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
            response = null;
        }

        if (extra != null) {
            Log.d(TAG, "Extra's were found.");
            if (extra.getBoolean(KEY_PROVIDE_UP_NAVIGATION, false)) {
                NavUtils.navigateUpFromSameTask(this);
            } else {
                finish();
            }
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customTabActivityHelper = null;
    }

    private class InfoTask extends AsyncTask<String, Void, Intent> {

        @Override
        protected Intent doInBackground(String... strings) {

            String token = strings[0];

            //First we get an auth code.
            try {
                Request<BearerToken> request = AccountUtils.buildAuthTokenRequest(token);
                BearerToken result = request.performRequest(null).getOrThrow();

                //Get the information
                UserInfoRequest infoRequest = new UserInfoRequest(result.getAccessToken());
                GrantInformation information = infoRequest.performRequest(null).getOrThrow();

                //Account name
                String name;
                if (information.getUserAttributes().getEmail().size() == 0) {
                    name = "Minerva-account";
                } else {
                    name = information.getUserAttributes().getEmail().get(0).toLowerCase();
                }

                //Save the account.
                Account account = new Account(name, accountType);
                if (getIntent().getBooleanExtra(ARG_ADDING_NEW_ACCOUNT, false)) {
                    manager.addAccountExplicitly(account, result.getRefreshToken(), null);
                } else {
                    manager.setPassword(account, result.getRefreshToken());
                }

                LocalDateTime expiration = LocalDateTime.now().plusSeconds(result.getExpiresIn());
                manager.setUserData(account, MinervaAuthenticator.EXP_DATE, expiration.format(MinervaAuthenticator.formatter));
                manager.setAuthToken(account, authType, result.getAccessToken());

                // Set the account's user name if present.
                if (information.getUserAttributes().getUid().size() >= 1) {
                    manager.setUserData(account, AccountUtils.USERNAME, information.getUserAttributes().getUid().get(0));
                }

                //Make intent for return value
                Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, name);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, result.getAccessToken());

                return res;
            } catch (RequestException e) {
                Log.w(TAG, "First request for account failed.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Intent intent) {
            if (intent == null) {
                finishWithError();
            } else {
                finishOK(intent);
            }
        }
    }
}