package be.ugent.zeus.hydra.requests.minerva;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;

import be.ugent.zeus.hydra.auth.AccountHelper;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.requests.common.TokenRequest;

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

    public MinervaRequest(Class<T> clazz, Context context, Activity activity) {
        super(clazz);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public String getToken() {
        return AccountHelper.asyncAuthCode(context, activity);
    }
}