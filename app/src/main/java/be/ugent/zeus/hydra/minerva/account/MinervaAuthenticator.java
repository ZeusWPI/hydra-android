package be.ugent.zeus.hydra.minerva.account;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import be.ugent.zeus.hydra.minerva.sync.network.auth.BearerToken;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.network.IOFailureException;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.minerva.sync.network.auth.AuthActivity;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Authenticator to save Minerva account details in the AccountManager. Minerva uses OAuth2 authentication with
 * the Code Grant authorization type (the user must consent to access).
 * Additionally, the authentication uses a refresh token and an access token. Read up on the OAuth2 spec for more
 * details, but in short the access token is used for the session while the refresh token is used as a sort of
 * password (to 'refresh' the access token).
 *
 * The app handles things things like this:
 * - The refresh token is saved as the account's password. This should be a long-lived token. When invalid, the user has re-authenticate.
 * - The access token is stored as the auth token.
 *
 * The full flow is as follows:
 *
 * 1. Open auth URL
 * 2. Receive an authorization code
 * 3. Save the user in the account manager.
 * 4. Use the authorisation code to get a bearer token. This contains both a refresh token and an access token.
 * 5. Save the access token as 'auth token' and the refresh token as password.
 * 6. Use the access token on the data requests.
 * 7. Optionally use the saved password to get a new bearer token, and go to step 5.
 *
 * Because the expiration date of the tokens is taken into consideration, you should not get an expired token often,
 * but is best to still be prepared for it.
 *
 * @see <a href="http://www.bubblecode.net/en/2016/01/22/understanding-oauth2/">Good explenation of OAuth2</a>
 *
 * @author Niko Strijbol
 */
public class MinervaAuthenticator extends AbstractAccountAuthenticator {

    private static final String TAG = MinervaAuthenticator.class.getSimpleName();

    public static final String EXP_DATE = "expDate";
    private static final String EXP_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(EXP_DATE_FORMAT);

    private Context mContext;
    private AccountManager manager;

    public MinervaAuthenticator(Context context) {
        super(context);
        this.mContext = context;
        this.manager = AccountManager.get(context);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, AuthActivity.class);
        intent.putExtra(AuthActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthActivity.ARG_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        if (options != null) {
            intent.putExtra(AuthActivity.ARG_EXTRA_BUNDLE, options);
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the access token for a given account.
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        final AccountManager am = AccountManager.get(mContext);

        //Get a access token from the cache.
        String accessToken = am.peekAuthToken(account, authTokenType);

        //Check the expiration date
        if(!TextUtils.isEmpty(accessToken)) {
            LocalDateTime expires = LocalDateTime.parse(manager.getUserData(account, EXP_DATE), formatter);
            LocalDateTime now = LocalDateTime.now();

            //The token is invalid, so get get new one.
            if(now.isAfter(expires)) {
                Log.d(TAG, "Expired token. Setting to null.");
                accessToken = null;
            }
        }

        //If there is no access token, we request a new one using the refresh token.
        //In this scenario there is no previous one.
        if(TextUtils.isEmpty(accessToken)) {
            String refreshToken = am.getPassword(account);
            if(refreshToken != null) {  //This should never be null actually.
                accessToken = getRefreshAccessToken(account, refreshToken);
                Log.d(TAG, "Got new access code for database account.");
            }
        } else {
            Log.d(TAG, "Used cached access code for database account.");
        }

        //If we got an access token, we return.
        if(!TextUtils.isEmpty(accessToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AuthActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthActivity.ARG_AUTH_TYPE, authTokenType);

        Log.d(TAG, "Account needs re-validation.");
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    /**
     * Get an access token from the refresh token and save the new data into the account.
     *
     * @param account The account.
     * @param refreshToken The refresh token.
     * @return The new access token or null on failure.
     *
     * @throws NetworkErrorException If the access token could not be retrieved due to network issues.
     */
    @VisibleForTesting
    String getRefreshAccessToken(Account account, String refreshToken) throws NetworkErrorException {

        //Make a request.
        Request<BearerToken> request = AccountUtils.buildRefreshTokenRequest(refreshToken);

        try {
            //Execute the request.
            BearerToken token = request.performRequest(null).getOrThrow();
            if (token.getRefreshToken() != null) {
                manager.setPassword(account, token.getRefreshToken());
            }

            LocalDateTime expiration = LocalDateTime.now().plusSeconds(token.getExpiresIn());
            manager.setUserData(account, EXP_DATE, expiration.format(formatter));

            return token.getAccessToken();
        } catch (IOFailureException e) {
            Log.w(TAG, "Could not get access token due to network failure.");
            throw new NetworkErrorException(e);
        } catch (RequestException e) {
            Log.i(TAG, "Getting refresh access token failed.", e);
            return null;
        }
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // We don't have multiple types in Hydra.
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        // We don't support features in Hydra.
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }
}