package be.ugent.zeus.hydra.fragments.urgent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.requests.UrgentUrlRequest;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.urgent.track.Track;

import java.io.IOException;

/**
 * @author Niko Strijbol
 */
public class UrgentTrack implements Track {

    private static final String TAG = "UrgentTrack";
    private static final int URGENT_ID = 1;

    private final Context context;

    UrgentTrack(Context context) {
        this.context = context.getApplicationContext();
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
                    return new UrgentUrlRequest(context).performRequest();
                } catch (RequestFailureException e) {
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
                    Log.w(TAG, "Error while doing URL", e);
                }
            }
        }.execute();
    }

    @Override
    @Nullable
    public Bitmap getAlbumArtwork() {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_urgent);
    }
}