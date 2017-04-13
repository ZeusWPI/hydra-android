package be.ugent.zeus.hydra.data.network.requests;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.IOFailureException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
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
    public String performRequest() throws RequestFailureException {
        try {
            URL url = new URL(Endpoints.URGENT_CONFIG_URL);
            return StringUtils.convertStreamToString(url.openStream()).trim();
        } catch (IOException e) {
            throw new IOFailureException(e);
        }
    }
}