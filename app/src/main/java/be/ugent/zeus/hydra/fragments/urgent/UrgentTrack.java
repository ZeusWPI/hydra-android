package be.ugent.zeus.hydra.fragments.urgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import be.ugent.zeus.hydra.urgent.track.Track;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static be.ugent.zeus.hydra.fragments.preferences.UrgentFragment.PREF_URGENT_USE_LOW_QUALITY;

/**
 * @author Niko Strijbol
 */
public class UrgentTrack implements Track {

    private static final String TAG = "UrgentTrack";
    private static final int URGENT_ID = 1;
    private static final String ARTWORK_URL = "http://urgent.fm/sites/all/themes/urgentfm/images/logo.jpg";
    private static final String CONFIG_URL = "http://urgent.fm/listen_live.config";

    private final Context context;

    public UrgentTrack(Context context) {
        this.context = context;
    }

    @Override
    public int getId() {
        return URGENT_ID;
    }

    @Override
    public String getArtist() {
        return null;
    }

    @Override
    public String getTitle() {
        return "Urgent.fm";
    }

    @Override
    public void getUrl(final UrlConsumer consumer) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(CONFIG_URL);
                    InputStream is = url.openStream();
                    String urlString = StringUtils.convertStreamToString(is);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    if(preferences.getBoolean(PREF_URGENT_USE_LOW_QUALITY, true) && NetworkUtils.isMeteredConnection(context)) {
                        Log.d(TAG, "Using low quality track.");
                        return urlString.replace("high", "low");
                    } else {
                        Log.d(TAG, "Using high quality track.");
                        return urlString;
                    }
                } catch (IOException e) {
                    Log.w(TAG, "Error while getting url: ", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    consumer.receive(s);
                } catch (IOException e) {
                    Log.e(TAG, "Error while doing URL", e);
                }
            }
        }.execute();
    }

    @Override
    public String getArtworkUrl() {
        return ARTWORK_URL;
    }
}