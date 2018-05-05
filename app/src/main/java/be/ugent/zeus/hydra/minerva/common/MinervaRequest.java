package be.ugent.zeus.hydra.minerva.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;

import be.ugent.zeus.hydra.common.network.IOFailureException;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.network.UnsuccessfulRequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.minerva.account.AccountUtils;
import be.ugent.zeus.hydra.minerva.account.MinervaConfig;
import com.crashlytics.android.Crashlytics;
import com.squareup.moshi.JsonAdapter;
import okhttp3.CacheControl;
import okhttp3.Request;

import java.io.IOException;

/**
 * Execute a request with a Minerva account. This class will inject the correct headers for authentication with the API.
 *
 * @author Niko Strijbol
 */
public abstract class MinervaRequest<T> extends JsonOkHttpRequest<T> {

    private static final String TAG = "MinervaRequest";

    private final AccountManager accountManager;
    private final Account account;
    private final AccessTokenProvider tokenProvider;

    /**
     * @param clazz The class of the result.
     * @param context The application context.
     * @param account The account to work with. Pass null to get the default account.
     */
    public MinervaRequest(Context context, Class<T> clazz, Account account) {
        this(context, clazz, account, AccountUtils::getAccessToken);
    }

    MinervaRequest(Context context, Class<T> clazz, Account account, AccessTokenProvider provider) {
        super(context, clazz);
        this.accountManager = AccountManager.get(context);
        this.account = account;
        this.tokenProvider = provider;
    }

    /**
     * Execute the request. If this fails, one possible reason is an expired auth token. In that case, we invalidate
     * our local token and try the request again. This will result in a new auth token being obtained if everything
     * goes well.
     */
    @NonNull
    @Override
    @WorkerThread
    public Result<T> performRequest(@NonNull Bundle args) {

        JsonAdapter<T> adapter = getAdapter();

        try {
            try {
                return executeRequest(adapter, args);
            } catch (UnsuccessfulRequestException e) {
                if (e.getHttpCode() != 503) {
                    throw e; // Proceed with error.
                }

                // TODO: log if this happens at all.
                // This should never be the case:
                // 1. If the access token is expired, the getToken() function will have caught and resolved it.
                // 2. If not resolvable, an AuthenticatorActionException will be thrown.
                Log.w(TAG, "Invalid auth token.", e);
                Crashlytics.log("There was an invalid token!");
                Crashlytics.logException(e);

                accountManager.invalidateAuthToken(MinervaConfig.ACCOUNT_TYPE, getToken());
                // Re-issue request.
                return executeRequest(adapter, args);
            }
        } catch (AuthenticatorActionException e) {
            Log.i(TAG, "Authenticator exception during request.", e);
            return Result.Builder.fromException(new AuthException(e));
        } catch (IOException | ConstructionException e) {
            Log.i(TAG, "An exception during request execution.", e);
            return Result.Builder.fromException(new IOFailureException(e));
        }
    }

    @NonNull
    @VisibleForTesting
    protected String getToken() throws ConstructionException {
        // Attempt to get the access token.
        Bundle tokenBundle = tokenProvider.getAccessTokenBundle(accountManager, account);

        // If the bundle contains an intent, the refresh token is invalid, so stop trying.
        if (tokenBundle.containsKey(AccountManager.KEY_INTENT)) {
            throw new AuthenticatorActionException();
        }

        String accessToken = tokenBundle.getString(AccountManager.KEY_AUTHTOKEN);

        // If the token is null, something else went wrong.
        if (accessToken == null) {
            throw new ConstructionException("Something went wrong while accessing the account.");
        }

        // Log the key for debug purposes.
        Log.d(TAG, "Minerva access token is: " + accessToken);
        return accessToken;
    }

    @Override
    protected CacheControl constructCacheControl(@NonNull Bundle arguments) {
        return CacheControl.FORCE_NETWORK; // No caching for Minerva.
    }

    @Override
    protected Request.Builder constructRequest(@NonNull Bundle arguments) throws ConstructionException {
        return super.constructRequest(arguments)
                .addHeader("Authorization", String.format("Bearer %s", getToken()));
    }

    /**
     * Wrapper interface for providing the request with an access token. Using this interface makes testing this
     * class a lot easier.
     */
    @FunctionalInterface
    protected interface AccessTokenProvider {
        /**
         * @see AccountUtils#getAccessToken(AccountManager, Account)
         */
        @NonNull
        Bundle getAccessTokenBundle(AccountManager accountManager, Account account);
    }

    private static class AuthenticatorActionException extends ConstructionException {
        AuthenticatorActionException() {
            super("The auth token requires user action.");
        }
    }
}