package be.ugent.zeus.hydra.requests.minerva;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.requests.exceptions.TokenException;
import be.ugent.zeus.hydra.requests.common.TokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Execute a request with a Minerva account.
 *
 * @author Niko Strijbol
 */
public abstract class MinervaRequest<T> extends TokenRequest<T> {

    private static final String TAG = "MinervaRequest";

    protected static final String MINERVA_API = "https://minerva.ugent.be/api/rest/v2/";

    protected final Context context;
    protected final Activity activity;
    protected final Account account;

    //This variable tracks if this is the first time the request is tried or not. This prevents endless loops if the
    //server is down for example.
    private boolean first = true;
    private Bundle accountBundle;

    /**
     * @param clazz The class of the result.
     * @param context The application context.
     * @param account The account to work with. Pass null to get the default account.
     * @param activity The activity to use for the account. If this is not null, the AccountManager may interact with
     *                 the user. If doing the request in the background, pass null.
     */
    public MinervaRequest(Class<T> clazz, Context context, @Nullable Account account, @Nullable Activity activity) {
        super(clazz);
        this.context = context;
        this.activity = activity;
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
        try {
            return super.performRequest();
        } catch (RequestFailureException e) {
            Log.i(TAG, "Request failed", e);

            //If is is a server error, the access token might be invalid.
            //We only try again one time.
            if(first && e.getCause() instanceof HttpServerErrorException) {
                HttpServerErrorException error = (HttpServerErrorException) e.getCause();
                if(error.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                    try {
                        Log.d("MinervaRequest", "Error while accessing stuff", e);
                        //Invalidate auth token and try again.
                        AccountManager m = AccountManager.get(context);
                        m.invalidateAuthToken(MinervaConfig.ACCOUNT_TYPE, getToken());
                        return performRequest();
                    } catch (TokenException e1) {
                        throw new RequestFailureException(e1);
                    } finally {
                        first = false;
                    }
                } else { //It was something else, like servers that don't work.
                    throw e;
                }
            } else {
                //It was something else, like the refresh token being expired.
                //In any case, the Bundle from the account manager has been set in this class.
                throw e;
            }
        }
    }

    @Override
    protected String getToken() throws TokenException {

        if(account == null) {
            accountBundle = AccountUtils.syncAuthCode(context);
        } else {
            accountBundle = AccountUtils.syncAuthCode(context, account);
        }

        if(accountBundle == null || accountBundle.containsKey(AccountManager.KEY_INTENT)) {
            throw new TokenException();
        } else {
            return accountBundle.getString(AccountManager.KEY_AUTHTOKEN);
        }
    }

    @Nullable
    public Bundle getAccountBundle() {
        return accountBundle;
    }
}