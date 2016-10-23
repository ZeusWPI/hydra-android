package be.ugent.zeus.hydra.fragments.urgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.urgent.track.Track;
import be.ugent.zeus.hydra.utils.NetworkUtils;

import static be.ugent.zeus.hydra.fragments.preferences.UrgentFragment.PREF_URGENT_USE_LOW_QUALITY;

/**
 * @author Niko Strijbol
 */
public class UrgentTrack implements Track {

    private static final String TAG = "UrgentTrack";
    private static final int URGENT_ID = 1;
    private static final String ARTWORK_URL = "http://urgent.fm/sites/all/themes/urgentfm/images/logo.jpg";
    private static final String MUSIC_URL_HIGH = "http://195.10.10.222/urgent/high.mp3";
    private static final String MUSIC_URL_LOW = "http://195.10.10.222/urgent/low.mp3";

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
    public String getUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(preferences.getBoolean(PREF_URGENT_USE_LOW_QUALITY, true) && NetworkUtils.isMeteredConnection(context)) {
            Log.d(TAG, "Using low quality track.");
            return MUSIC_URL_LOW;
        } else {
            Log.d(TAG, "Using high quality track.");
            return MUSIC_URL_HIGH;
        }
    }

    @Override
    public String getArtworkUrl() {
        return ARTWORK_URL;
    }
}