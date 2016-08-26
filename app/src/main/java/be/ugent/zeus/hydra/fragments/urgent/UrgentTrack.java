package be.ugent.zeus.hydra.fragments.urgent;

import com.mylovemhz.simplay.Track;

/**
 * @author Niko Strijbol
 */
public class UrgentTrack implements Track {

    public static int URGENT_ID = 1;
    public static String ARTWORK_URL = "http://urgent.fm/sites/all/themes/urgentfm/images/logo.jpg";
    public static final String MUSIC_URL = "http://195.10.2.96/urgent/high.mp3";

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
        return "Schamper";
    }

    @Override
    public String getUrl() {
        return MUSIC_URL;
    }

    @Override
    public String getArtworkUrl() {
        return ARTWORK_URL;
    }
}