package be.ugent.zeus.hydra.data.network.requests;

import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.IOFailureException;
import be.ugent.zeus.hydra.utils.StringUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Request to get the correct URL for the Urgent.fm stream. This class takes the quality settings into account.
 *
 * @author Niko Strijbol
 */
public class UrgentUrlRequest implements Request<String> {

    @NonNull
    @Override
    public Result<String> performRequest(Bundle args) {
        try {
            URL url = new URL(Endpoints.URGENT_CONFIG_URL);
            return new Result.Builder<String>()
                    .withData(StringUtils.convertStreamToString(url.openStream()).trim())
                    .build();
        } catch (IOException e) {
            return new Result.Builder<String>()
                    .withError(new IOFailureException(e))
                    .build();
        }
    }
}