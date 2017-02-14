package be.ugent.zeus.hydra.minerva.requests;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.minerva.auth.AuthenticatorActionException;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.minerva.auth.requests.TokenRequestInterceptor;
import be.ugent.zeus.hydra.requests.common.JsonSpringRequest;
import be.ugent.zeus.hydra.requests.exceptions.IOFailureException;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * Execute a request with a Minerva account.
 *
 * @author Niko Strijbol
 */
public abstract class MinervaRequest<T> extends JsonSpringRequest<T> {

    private static final String TAG = "MinervaRequest";

    protected static final String MINERVA_API = "https://minerva.ugent.be/api/rest/v2/";

    protected final Context context;
    protected final Account account;

    //This variable tracks if this is the first time the request is tried or not. This prevents endless loops if the
    //server is down for example.
    private boolean first = true;
    private Bundle accountBundle;

    /**
     * @param clazz The class of the result.
     * @param context The application context.
     * @param account The account to work with. Pass null to get the default account.
     */
    public MinervaRequest(Class<T> clazz, Context context, Account account) {
        super(clazz);
        this.context = context;
        this.account = account;
    }

    /**
     * {@inheritDoc}
     *
     * After the first try, this function requests new credentials, and tries the request again, because the first
     * time may have failed because the saved OAuth keys are invalid.
     *
     * If the request still fails after the first time or new OAuth keys could not be obtained, the method will throw
     * the exception (same behavior as the parent method).
     */
    @NonNull
    @Override
    public T performRequest() throws RequestFailureException {
        //Try the request one time.
        try {
            return super.performRequest();
        } catch (AuthenticatorActionException e) {
            Log.i(TAG, "User action is required.");
            //In this case we need user interaction, so we must not try again.
            throw e;
        } catch (IOFailureException e) {
            Log.i(TAG, "Network failure.");
            //The network failed, don't try again.
            throw e;
        } catch (RequestFailureException e) {
            Log.i(TAG, "Request failed", e);

            //If is is a server error, the access token might be invalid.
            //We only try again one time.
            if(first && e.getCause() instanceof HttpServerErrorException) {
                HttpServerErrorException error = (HttpServerErrorException) e.getCause();
                if(error.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                    try {
                        Log.d("MinervaRequest", "Invalid token while accessing stuff", e);
                        //Invalidate auth token and try again.
                        AccountManager m = AccountManager.get(context);
                        m.invalidateAuthToken(MinervaConfig.ACCOUNT_TYPE, getToken());
                        return performRequest();
                    } finally {
                        first = false;
                    }
                } else {
                    //It was something else, like servers that don't work.
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

    @NonNull
    private String getToken() throws RequestFailureException {

        //First we get the token to insert to this request.
        try {
            accountBundle = AccountUtils.syncAuthCode(context, account);
        } catch (IOException e) {
            throw new IOFailureException(e);
        }

        if(accountBundle == null) {
            throw new RequestFailureException("The account not be read.");
        }

        //If the bundle contains an intent, we throw an error.
        if (accountBundle.containsKey(AccountManager.KEY_INTENT)) {
            throw new AuthenticatorActionException();
        } else { //Otherwise we can proceed.
            String token = accountBundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.d(TAG, "Minerva auth key: " + token);
            return token;
        }
    }

    @Override
    protected RestTemplate createRestTemplate() throws RequestFailureException {
        RestTemplate t = super.createRestTemplate();
        t.setInterceptors(Collections.singletonList(new TokenRequestInterceptor(getToken())));
        return t;
    }

    /**
     * Get the account bundle. This will be set if the AccountManager determined interaction with the user is
     * needed. If the request completed successfully, this will be null.
     *
     * @return The account bundle on failure, or null on success.
     */
    @Nullable
    public Bundle getAccountBundle() {
        return accountBundle;
    }
}