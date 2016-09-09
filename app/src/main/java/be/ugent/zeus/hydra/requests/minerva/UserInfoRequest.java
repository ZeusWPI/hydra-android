package be.ugent.zeus.hydra.requests.minerva;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.auth.MinervaConfig;
import be.ugent.zeus.hydra.auth.models.GrantInformation;
import be.ugent.zeus.hydra.requests.common.TokenRequest;

/**
 * This is the user information request. This is a special request that needs a token, since this request is part of
 * the account creation process and is called before the account is saved on the device.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class UserInfoRequest extends TokenRequest<GrantInformation> {

    private String token;

    /**
     * @param token The access token to use with the request.
     */
    public UserInfoRequest(String token) {
        super(GrantInformation.class);
        this.token = token;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MinervaConfig.GRANT_INFORMATION_ENDPOINT;
    }

    /**
     * Get the token to use in the request.
     *
     * @return The token.
     */
    @Override
    protected String getToken() {
        return token;
    }
}