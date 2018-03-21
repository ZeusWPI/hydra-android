package be.ugent.zeus.hydra.minerva.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.minerva.account.MinervaConfig;
import be.ugent.zeus.hydra.minerva.common.MinervaRequest;
import okhttp3.CacheControl;
import okhttp3.Request;

/**
 * This is the user information request. This is a special request that needs a token, since this request is part of
 * the account creation process and is called before the account is saved on the device.
 *
 * All other requests to Minerva should use the account functionality, which is provided in {@link MinervaRequest}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class UserInfoRequest extends JsonOkHttpRequest<GrantInformation> {

    private final String token;

    /**
     * @param token The access token to use with the request.
     */
    UserInfoRequest(Context context, String token) {
        super(context, GrantInformation.class);
        this.token = token;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MinervaConfig.GRANT_INFORMATION_ENDPOINT;
    }

    @Override
    protected Request.Builder constructRequest(@NonNull Bundle arguments) throws ConstructionException {
        return super.constructRequest(arguments)
                .addHeader("Authorization", String.format("Bearer %s", token));
    }

    @Override
    protected CacheControl constructCacheControl(@NonNull Bundle arguments) {
        return CacheControl.FORCE_NETWORK;
    }
}