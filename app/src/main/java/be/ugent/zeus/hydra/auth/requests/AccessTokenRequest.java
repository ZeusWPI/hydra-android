package be.ugent.zeus.hydra.auth.requests;

import java.io.IOException;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.auth.OAuthConfiguration;
import be.ugent.zeus.hydra.auth.models.BearerToken;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.requests.common.Request;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Exchange an authorisation code for an access code.
 *
 * @author Niko Strijbol
 */
public abstract class AccessTokenRequest implements Request<BearerToken> {

    protected OAuthConfiguration configData;
    protected String code;

    public AccessTokenRequest(OAuthConfiguration configData, String code) {
        this.configData = configData;
        this.code = code;
    }

    @NonNull
    @Override
    public BearerToken performRequest() throws RequestFailureException {
        OAuthJSONAccessTokenResponse accessTokenResponse;

        try {
            accessTokenResponse = getToken();
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(accessTokenResponse.getBody(), BearerToken.class);
        } catch (IOException | OAuthSystemException | OAuthProblemException e) {
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