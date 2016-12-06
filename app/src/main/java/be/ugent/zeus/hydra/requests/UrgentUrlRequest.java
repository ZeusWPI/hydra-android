package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.IOFailureException;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.utils.StringUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Request to get the correct URL for the Urgent.fm stream. This class takes the quality settings into account.
 *
 * @author Niko Strijbol
 */
public class UrgentUrlRequest implements Request<String> {

    private static final String CONFIG_URL = "http://urgent.fm/listen_live.config";

    @NonNull
    @Override
    public String performRequest() throws RequestFailureException {
        try {
            URL url = new URL(CONFIG_URL);
            return StringUtils.convertStreamToString(url.openStream());
        } catch (IOException e) {
            throw new IOFailureException(e);
        }
    }
}