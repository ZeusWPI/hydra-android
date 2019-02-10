package be.ugent.zeus.hydra.minerva.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.minerva.auth.oauth.BearerToken;
import be.ugent.zeus.hydra.minerva.auth.oauth.NewAccessTokenRequest;
import be.ugent.zeus.hydra.minerva.auth.oauth.RefreshAccessTokenRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;

import static be.ugent.zeus.hydra.minerva.account.MinervaAuthenticator.EXP_DATE;

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
     * Try to get a valid access token. This method assumes the {@code account} has already been added to the
     * AccountManager.
     *
     * This method will retrieve the current access token. If it is not available or expired, the method will
     * invalidate the access token and request another one. If there still isn't any token available, it will
     * return the bundle from the AccountManager.
     *
     * If the refresh token is also expired (last scenario above), a notification will be shown by the AccountManager.
     *
     * @param manager The account manager.
     * @param account The account.
     *
     * @return The bundle containing the access code, or an intent to re-authorise the account.
     */
    @NonNull
    public static Bundle getAccessToken(AccountManager manager, Account account) {
        try {
            Bundle result = manager
                    .getAuthToken(account, MinervaConfig.DEFAULT_SCOPE, null, true, null, null)
                    .getResult();

            String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

            if (authToken != null) {
                // Get the expiration date from the account manager.
                LocalDateTime expires = getExpirationDate(manager, account);
                LocalDateTime now = LocalDateTime.now();

                // We consider a token expired if e don't have any date or it is actually expired.
                if (expires == null || now.isAfter(expires)) {
                    Log.d(TAG, "The access token is expired. Invalidating and requesting a new one.");
                    manager.invalidateAuthToken(MinervaConfig.ACCOUNT_TYPE, authToken);
                    result = manager
                            .getAuthToken(account, MinervaConfig.DEFAULT_SCOPE, null, true, null, null)
                            .getResult();
                }
            }

            return result;
        } catch (OperationCanceledException | AuthenticatorException | IOException e) {
            Log.w(TAG, "Getting result failed.", e);
            return Bundle.EMPTY;
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

    private static final String EXP_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(EXP_DATE_FORMAT);

    /**
     * Get the expiration date of the access token for an account.
     *
     * @param manager The account manager.
     * @param account The account to get the date for.
     *
     * @return The date or null if there was no date.
     */
    @Nullable
    public static LocalDateTime getExpirationDate(AccountManager manager, Account account) {
        String exp = manager.getUserData(account, EXP_DATE);
        if (exp == null) {
            return null;
        } else {
            return LocalDateTime.parse(exp, FORMATTER);
        }
    }

    /**
     * Get the expiration date of the access token for an account.
     *
     * @param manager The account manager.
     * @param account The account to get the date for.
     */
    public static void setExpirationDate(AccountManager manager, Account account, LocalDateTime date) {
        manager.setUserData(account, EXP_DATE, date.format(FORMATTER));
    }
}