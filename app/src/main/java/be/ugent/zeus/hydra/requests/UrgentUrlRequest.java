package be.ugent.zeus.hydra.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.IOFailureException;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static be.ugent.zeus.hydra.fragments.preferences.UrgentFragment.PREF_URGENT_USE_LOW_QUALITY;

/**
 * Request to get the correct URL for the Urgent.fm stream. This class takes the quality settings into account.
 *
 * @author Niko Strijbol
 */
public class UrgentUrlRequest implements Request<String> {

    private static final String TAG = "UrgentUrlRequest";
    private static final String CONFIG_URL = "http://urgent.fm/listen_live.config";

    private Context context;

    public UrgentUrlRequest(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String performRequest() throws RequestFailureException {
        try {
            URL url = new URL(CONFIG_URL);
            InputStream is = url.openStream();
            String urlString = StringUtils.convertStreamToString(is);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean(PREF_URGENT_USE_LOW_QUALITY, true) && NetworkUtils.isMeteredConnection(context)) {
                Log.d(TAG, "Using low quality track.");
                return urlString.replace("high", "low");
            } else {
                Log.d(TAG, "Using high quality track.");
                return urlString;
            }
        } catch (IOException e) {
            throw new IOFailureException(e);
        }
    }
}
