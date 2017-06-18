package be.ugent.zeus.hydra.data.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.minerva.auth.BearerToken;
import be.ugent.zeus.hydra.data.network.requests.minerva.auth.NewAccessTokenRequest;
import be.ugent.zeus.hydra.data.network.requests.minerva.auth.RefreshAccessTokenRequest;
import be.ugent.zeus.hydra.repository.requests.Request;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.threeten.bp.LocalDateTime;

import java.io.IOException;

import static be.ugent.zeus.hydra.data.auth.MinervaAuthenticator.EXP_DATE;

/**
 * Helper class for working with a Minerva account.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
public class AccountUtils {

    private static final String TAG = "AccountUtils";

    //This never changes during runtime, so we use static here.
    private static final OAuthConfiguration config = new OAuthConfiguration.Builder()
            .apiKey(BuildConfig.OAUTH_ID)
            .apiSecret(BuildConfig.OAUTH_SECRET)
            .callbackUri(MinervaConfig.CALLBACK_URI)
            .build();

    /**
     * Builds a token request based on the grant information.
     *
     * @return Request based on an authorization token.
     */
    public static Request<BearerToken> buildAuthTokenRequest(String authorizationCode) {
        return new NewAccessTokenRequest(config, authorizationCode);
    }

    /**
     * Builds a token request based on the refresh token.
     *
     * @return Request based on a refresh token.
     */
    public static Request<BearerToken> buildRefreshTokenRequest(String authorizationCode) {
        return new RefreshAccessTokenRequest(config, authorizationCode);
    }

    /**
     * @return The URI to be displayed to use authorize use.
     */
    public static String getRequestUri() {
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation(MinervaConfig.AUTHORIZATION_ENDPOINT)
                    .setResponseType(ResponseType.CODE.toString())
                    .setClientId(config.API_KEY)
                    .setRedirectURI(config.CALLBACK_URI)
                    .setState("auth_state")
                    .buildQueryMessage();
            return request.getLocationUri();
        } catch (OAuthSystemException e) {
            Log.e(TAG, "Error while building URI", e);
            //This shouldn't happen, so we intentionally crash the app.
            throw new IllegalStateException("This must not happen!", e);
        }
    }

    /**
     * Check if a Minerva account is present.
     *
     * @param context The application context.
     *
     * @return True if there is an account, otherwise false.
     */
    public static boolean hasAccount(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType(MinervaConfig.ACCOUNT_TYPE);
        return accounts.length >= 1;
    }

    /**
     * Get an access token. This is executed in a blocking manner. This method assumes an account is present. Use
     * the method {@link #hasAccount(Context)} to find out if there actually is an account.
     *
     * This method does not take an activity, and always returns the bundle instead.
     *
     * @see #syncAuthCode(Context, Account)
     *
     * @param context The application context.
     *
     * @return The bundle containing the access code, or an intent to re-authorise the account.
     */
    public static Bundle syncAuthCode(Context context) throws IOException {
        AccountManager manager = AccountManager.get(context);
        Account account = manager.getAccountsByType(MinervaConfig.ACCOUNT_TYPE)[0];

        return syncAuthCode(context, account);
    }

    /**
     * Get an access token. This is executed in a blocking manner. This method assumes an account is present. Use
     * the method {@link #hasAccount(Context)} to find out if there actually is an account.
     *
     * This method does not take an activity, and always returns the bundle instead.
     *
     * @param context The application context.
     * @param account The account.
     *
     * @return The bundle containing the access code, or an intent to re-authorise the account.
     */
    public static Bundle syncAuthCode(Context context, Account account) throws IOException {
        AccountManager manager = AccountManager.get(context);

        try {
            Bundle result = manager.getAuthToken(account, MinervaConfig.DEFAULT_SCOPE, null, true, null, null).getResult();

            //If the bundle contains an authorisation code.
            if(result.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                //Check the expiration date
                LocalDateTime expires = getExpirationDate(manager, account);
                LocalDateTime now = LocalDateTime.now();

                String token = result.getString(AccountManager.KEY_AUTHTOKEN);

                //The token is invalid, so get get new one.
                if(result.get(AccountManager.KEY_AUTHTOKEN) != null && now.isAfter(expires)) {
                    Log.d(TAG, "Expired token. Setting to null.");
                    manager.invalidateAuthToken(MinervaConfig.ACCOUNT_TYPE, token);
                    //Get the token again.
                    result = manager.getAuthToken(account, MinervaConfig.DEFAULT_SCOPE, null, null, null, null).getResult();
                }
            }

            return result;

        } catch (OperationCanceledException | AuthenticatorException e) {
            Log.w(TAG, "Getting result failed.", e);
            return null;
        }
    }

    /**
     * Get the account. This assumes there is an account.
     * @param context The context.
     * @return The account.
     */
    public static Account getAccount(Context context) {
       return AccountManager.get(context).getAccountsByType(MinervaConfig.ACCOUNT_TYPE)[0];
    }

    /**
     * Get the expiration date of the access token for an account.
     *
     * @param manager The account manager.
     *
     * @param account The account to get the date for.
     * @return The date.
     */
    public static LocalDateTime getExpirationDate(AccountManager manager, Account account) {
        String exp = manager.getUserData(account, EXP_DATE);
        return LocalDateTime.parse(exp, MinervaAuthenticator.formatter);
    }
}