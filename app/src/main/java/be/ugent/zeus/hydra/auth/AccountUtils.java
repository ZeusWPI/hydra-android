package be.ugent.zeus.hydra.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.auth.models.BearerToken;
import be.ugent.zeus.hydra.auth.requests.NewAccessTokenRequest;
import be.ugent.zeus.hydra.auth.requests.RefreshAccessTokenRequest;
import be.ugent.zeus.hydra.cache.file.FileCache;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.minerva.WhatsNewRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.joda.time.DateTime;

import java.io.IOException;

import static be.ugent.zeus.hydra.auth.MinervaAuthenticator.EXP_DATE;

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
            .callbackUri(EndpointConfiguration.CALLBACK_URI)
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
     * Builds a token request based on the grant information.
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
                    .authorizationLocation(EndpointConfiguration.AUTHORIZATION_ENDPOINT)
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
        Account[] accounts = manager.getAccountsByType(EndpointConfiguration.ACCOUNT_TYPE);
        if(accounts.length >= 1) {
            return true;
        } else {
            //If there is no account, we delete the cache immediately.
            //Delete list of courses
            FileCache.deleteStartingWith(CoursesMinervaRequest.BASE_KEY, context.getApplicationContext());
            //Delete all courses
            FileCache.deleteStartingWith(WhatsNewRequest.BASE_KEY, context.getApplicationContext());
            return false;
        }
    }

    /**
     * Get an access token. This is executed in a blocking manner. This method assumes an account is present. Use
     * the method {@link #hasAccount(Context)} to find out if there actually is an account.
     *
     * @param context The application context.
     * @param activity The current foreground activity, if you want the account manager to ask the user things.
     *
     * @return The access token, or null if there is none.
     */
    @Nullable
    public static String asyncAuthCode(Context context, Activity activity) {
        AccountManager manager = AccountManager.get(context);
        Account account = manager.getAccountsByType(EndpointConfiguration.ACCOUNT_TYPE)[0];

        return asyncAuthCode(context, account, activity);
    }

    /**
     * Get an access token. This is executed in a blocking manner. This method assumes an account is present. Use
     * the method {@link #hasAccount(Context)} to find out if there actually is an account.
     *
     * @param context The application context.
     * @param account The account.
     * @param activity The current foreground activity, if you want the account manager to ask the user things.
     *
     * @return The access token, or null if there is none.
     */
    @Nullable
    public static String asyncAuthCode(Context context, Account account, Activity activity) {
        AccountManager manager = AccountManager.get(context);

        try {
            Bundle result = manager.getAuthToken(account, EndpointConfiguration.DEFAULT_SCOPE, null, activity, null, null).getResult();
            String token = result.getString(AccountManager.KEY_AUTHTOKEN);
            Log.d(TAG, "Got bundle.");

            //Check the expiration date
            DateTime expires = getExpirationDate(manager, account);
            DateTime now = DateTime.now();

            //The token is invalid, so get get new one.
            if(result.get(AccountManager.KEY_AUTHTOKEN) != null && now.isAfter(expires)) {
                Log.d(TAG, "Expired token. Setting to null.");
                manager.invalidateAuthToken(EndpointConfiguration.ACCOUNT_TYPE, token);
                //Get the token again.
                result = manager.getAuthToken(account, EndpointConfiguration.DEFAULT_SCOPE, null, activity, null, null).getResult();
                token = result.getString(AccountManager.KEY_AUTHTOKEN);
            }

            return token;

        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
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
       return AccountManager.get(context).getAccountsByType(EndpointConfiguration.ACCOUNT_TYPE)[0];
    }

    /**
     * Get the expiration date of the access token for an account.
     *
     * @param manager The account manager.
     *
     * @param account The account to get the date for.
     * @return The date.
     */
    public static DateTime getExpirationDate(AccountManager manager, Account account) {
        String exp = manager.getUserData(account, EXP_DATE);
        return MinervaAuthenticator.formatter.parseDateTime(exp);
    }
}