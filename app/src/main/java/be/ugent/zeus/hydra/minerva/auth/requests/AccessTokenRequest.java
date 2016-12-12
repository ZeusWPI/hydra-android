package be.ugent.zeus.hydra.minerva.auth.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.auth.OAuthConfiguration;
import be.ugent.zeus.hydra.minerva.auth.models.BearerToken;
import be.ugent.zeus.hydra.requests.exceptions.IOFailureException;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.requests.common.Request;
import com.google.gson.Gson;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

/**
 * Exchange an authorisation code for an access code.
 *
 * @author Niko Strijbol
 */
public abstract class AccessTokenRequest implements Request<BearerToken> {

    protected OAuthConfiguration configData;
    protected String code;
    private Gson gson;

    public AccessTokenRequest(OAuthConfiguration configData, String code) {
        this.configData = configData;
        this.code = code;
        this.gson = new Gson();
    }

    @NonNull
    @Override
    public BearerToken performRequest() throws RequestFailureException {
        try {
            OAuthJSONAccessTokenResponse accessTokenResponse = getToken();
            return gson.fromJson(accessTokenResponse.getBody(), BearerToken.class);
        } catch (OAuthSystemException e) {
            throw new IOFailureException(e);
        } catch (OAuthProblemException e) {
            throw new RequestFailureException(e);
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