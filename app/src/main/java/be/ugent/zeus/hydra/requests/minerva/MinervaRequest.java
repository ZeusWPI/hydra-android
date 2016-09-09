package be.ugent.zeus.hydra.requests.minerva;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import be.ugent.zeus.hydra.auth.AccountUtils;
import be.ugent.zeus.hydra.auth.MinervaConfig;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;
import be.ugent.zeus.hydra.requests.common.TokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Execute a request with a minerva account.
 *
 * @author Niko Strijbol
 */
public abstract class MinervaRequest<T> extends TokenRequest<T> {

    private static final String TAG = "MinervaRequest";

    protected static final String MINERVA_API = "https://minqas.ugent.be/api/rest/v2/";

    protected final Context context;
    protected final Activity activity;
    protected final Account account;

    /**
     * @param clazz The class of the result.
     * @param context The application context.
     * @param activity The activity to use for the account. If this is not null, the AccountManager may interact with
     *                 the user. If doing the request in the background, pass null.
     */
    public MinervaRequest(Class<T> clazz, Context context, @Nullable Activity activity) {
        this(clazz, context, null, activity);
    }

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

    private boolean first = true;

    @NonNull
    @Override
    public T performRequest() throws RequestFailureException {
        try {
            return super.performRequest();
        } catch (RequestFailureException e) {
            Log.i(TAG, "Request failed", e);
            if(first && e.getCause() instanceof HttpServerErrorException) {
                HttpServerErrorException error = (HttpServerErrorException) e.getCause();
                if(error.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                    Log.d("MinervaRequest", "Error while accessing stuff", e);
                    //Invalidate auth token and try again.
                    AccountManager m = AccountManager.get(context);
                    m.invalidateAuthToken(MinervaConfig.ACCOUNT_TYPE, getToken());
                    first = false;
                    return performRequest();
                }
                throw e;
            } else {
                throw e;
            }
        }
    }

    @Override
    protected String getToken() {
        if(account == null) {
            return AccountUtils.syncAuthCode(context, activity);
        } else {
            return AccountUtils.syncAuthCode(context, account, activity);
        }
    }
}