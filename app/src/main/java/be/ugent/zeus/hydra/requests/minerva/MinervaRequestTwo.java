package be.ugent.zeus.hydra.requests.minerva;

import java.io.Serializable;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.android.sdk.oauth.request.ManualAccessTokenRequestInterceptor;
import be.ugent.zeus.hydra.auth.AccountHelper;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.requests.AbstractRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * Created by feliciaan on 20/06/16.
 */
public abstract class MinervaRequestTwo<T extends Serializable> extends AbstractRequest<T> {

    private static final String TAG = "MinervaRequest";

    protected static final String MINERVA_API = "https://minqas.ugent.be/api/rest/v2/";

    protected Context context;
    protected Activity activity;
    private String token;

    public MinervaRequestTwo(Class<T> clazz, Context context, Activity activity) {
        super(clazz);
        this.context = context;
        this.activity = activity;
    }

    /**
     * If there is no valid token, we first get a new one. Then we execute the current request.
     */
    @NonNull
    @Override
    public T performRequest() throws RequestFailureException {
        Log.d(TAG, "Minerva data requested.");
        token = AccountHelper.asyncAuthCode(context, activity);
        Log.d(TAG, "Got token " + token);
        return super.performRequest();
    }

    /**
     * Set the API interceptor.
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate t = super.createRestTemplate();
        t.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new ManualAccessTokenRequestInterceptor(token)));
        return t;
    }
}
