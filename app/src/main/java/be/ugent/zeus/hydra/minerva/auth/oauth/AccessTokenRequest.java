package be.ugent.zeus.hydra.minerva.auth.oauth;

import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.IOFailureException;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.minerva.account.OAuthConfiguration;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.io.IOException;

/**
 * Exchange an authorisation code for an access code.
 *
 * @author Niko Strijbol
 */
abstract class AccessTokenRequest implements Request<BearerToken> {

    @SuppressWarnings("WeakerAccess")
    protected final OAuthConfiguration configData;
    protected final String code;
    private final JsonAdapter<BearerToken> jsonAdapter;

    AccessTokenRequest(OAuthConfiguration configData, String code) {
        this.configData = configData;
        this.code = code;
        this.jsonAdapter = new Moshi.Builder().build().adapter(BearerToken.class);
    }

    @NonNull
    @Override
    public Result<BearerToken> execute(@NonNull Bundle args) {
        try {
            OAuthJSONAccessTokenResponse accessTokenResponse = getToken();
            BearerToken token = jsonAdapter.fromJson(accessTokenResponse.getBody());
            if (token == null) {
                throw new IOException("Error while reading code!");
            }
            return new Result.Builder<BearerToken>()
                    .withData(token)
                    .build();
        } catch (OAuthSystemException | IOException e) {
            return new Result.Builder<BearerToken>()
                    .withError(new IOFailureException(e))
                    .build();
        } catch (OAuthProblemException e) {
            return new Result.Builder<BearerToken>()
                    .withError(new RequestException(e))
                    .build();
        }
    }

    /**
     * Get the token.
     *
     * @return The token data.
     *
     * @throws OAuthProblemException See documentation of {@link OAuthProblemException}.
     * @throws OAuthSystemException See documentation of {@link OAuthSystemException}.
     */
    protected abstract OAuthJSONAccessTokenResponse getToken() throws OAuthProblemException, OAuthSystemException;
}