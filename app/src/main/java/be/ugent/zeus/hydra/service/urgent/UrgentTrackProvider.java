package be.ugent.zeus.hydra.service.urgent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.UrgentInfo;
import be.ugent.zeus.hydra.data.network.requests.UrgentInfoRequest;
import be.ugent.zeus.hydra.repository.requests.Result;
import java8.util.function.Consumer;

/**
 * @author Niko Strijbol
 */
public class UrgentTrackProvider {

    public static final String URGENT_ID = "be.ugent.zeus.hydra.urgent";

    public static final String MEDIA_ID_ROOT = "__ROOT__";
    public static final String MEDIA_ID_EMPTY_ROOT = "__EMPTY__";

    private MediaMetadataCompat track;
    private final Context context;

    public UrgentTrackProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    public MediaMetadataCompat getTrack() {
        return track;
    }

    public boolean isUrgentId(String id) {
        return URGENT_ID.equals(id);
    }

    public void prepareMedia(@NonNull Consumer<Boolean> callback) {

        if (track != null) {
            callback.accept(true);
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                loadData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                callback.accept(hasTrackInformation());
            }
        }.execute();
    }

    private synchronized void loadData() {
        UrgentInfoRequest infoRequest = new UrgentInfoRequest();
        Result<UrgentInfo> programme = infoRequest.performRequest(null);

        if (!programme.hasData()) {
            // It failed.
            return;
        }
        UrgentInfo info = programme.getData();

        Bitmap albumArt = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_urgent);
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, URGENT_ID)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, info.getUrl())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);

        if (!TextUtils.isEmpty(info.getName())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, info.getName())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, context.getString(R.string.urgent_fm));
        } else {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, context.getString(R.string.urgent_fm));
        }

        track = builder.build();
    }

    public synchronized boolean hasTrackInformation() {
        return track != null;
    }
}