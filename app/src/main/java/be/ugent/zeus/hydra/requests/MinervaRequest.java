package be.ugent.zeus.hydra.requests;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import be.ugent.android.sdk.oauth.exception.OAuthException;
import be.ugent.android.sdk.oauth.exception.OAuthTokenException;
import be.ugent.android.sdk.oauth.json.BearerToken;
import be.ugent.zeus.hydra.fragments.AbstractSpiceRequest;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import com.google.inject.Inject;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import roboguice.RoboGuice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by feliciaan on 20/06/16.
 */
public abstract class MinervaRequest<T extends Serializable> extends AbstractSpiceRequest<T> {

    private static final String TAG = "MinervaRequest";

    protected static final String MINERVA_API = "https://minqas.UGent.be/api/rest/v2/";

    @Inject protected AuthorizationManager authorizationManager;

    public MinervaRequest(Class<T> clazz, Context context) {
        super(clazz);
        RoboGuice.getInjector(context.getApplicationContext()).injectMembers(this);
    }

    protected Map<String, String> getURLVariables() {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("access_token", authorizationManager.getAccessToken());
        return urlVariables;
    }

    @NonNull
    @Override
    public T performRequest() throws RequestFailureException {
        throw new UnsupportedOperationException();
    }

    public void execute(final SpiceManager spiceManager, final RequestListener<T> requestListener) {
        /**
         * Dynamically built request listener that will handle the refreshing
         * of the access tokens. When the token has become invalid, it will first
         * execute a refresh token request and perform the original request only
         * after a successful retrieval of a new token.
         *
         * On Failure:
         *  - Figure out what went wrong with the Bearer token request.
         *  - Notify the original request listener of the request failure
         *
         * On Success:
         *  - Save the newly received token.
         *  - Perform the original request with the original request listener.
         */
        class BearerTokenRequestListener implements RequestListener<BearerToken> {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, "Failed retrieval of next token.");
                // handle change of client credentials
                try {
                    OAuthProblemException oAuthProblemException =
                            (OAuthProblemException) spiceException.getCause();
                    if (oAuthProblemException.getError().equals("invalid_client")) {
                        requestListener.onRequestFailure(new OAuthTokenException(
                                OAuthException.ERROR.CREDENTIALS_CHANGED
                        ));
                        return;
                    }
                } catch (Exception e) {
                    // Was it something else? Log it.
                    e.printStackTrace();
                }

                // default error to unknown
                requestListener.onRequestFailure(new OAuthTokenException(
                        OAuthException.ERROR.UNKNOWN_ERROR
                ));
            }

            @Override
            public void onRequestSuccess(BearerToken bearerToken) {
                authorizationManager.setBearerToken(bearerToken);
                Log.d(TAG, "Executing user request.");
                spiceManager.execute(MinervaRequest.this,
                        getCacheKey(),
                        getCacheDuration(),
                        requestListener);
            }
        }

        if (!authorizationManager.isAuthenticated() && authorizationManager.getAccessToken() != null) {
            // perform a bearer token request prior to performing the initial request.
            spiceManager.execute(
                    authorizationManager.buildTokenRefreshRequest(),
                    null,
                    DurationInMillis.ONE_HOUR,
                    new BearerTokenRequestListener());
        } else {
            // perform the initial request
            spiceManager.execute(MinervaRequest.this,
                    getCacheKey(),
                    getCacheDuration(),
                    requestListener);
        }
    }
}
