package be.ugent.zeus.hydra.requests.minerva;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.auth.AccountUtils;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.requests.common.TokenRequest;

import java.io.Serializable;

/**
 * Request for Minerva data, using the account managers token. This request assumes a valid account is present. It is up
 * to the caller to make sure this is the case.
 *
 * @author Niko Strijbol
 */
public abstract class MinervaRequest<T extends Serializable> extends TokenRequest<T> implements CacheRequest<T> {

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

    @Override
    protected String getToken() {
        if(account == null) {
            return AccountUtils.asyncAuthCode(context, activity);
        } else {
            return AccountUtils.asyncAuthCode(context, account, activity);
        }
    }
}