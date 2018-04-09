package be.ugent.zeus.hydra.urgent;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import okhttp3.CacheControl;

/**
 * @author Niko Strijbol
 */
public class UrgentInfoRequest extends JsonOkHttpRequest<UrgentInfo> {

    public UrgentInfoRequest(Context context) {
        super(context, UrgentInfo.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.URGENT_STATUS_URL;
    }

    @Override
    protected CacheControl constructCacheControl(@NonNull Bundle arguments) {
        return CacheControl.FORCE_NETWORK;
    }
}