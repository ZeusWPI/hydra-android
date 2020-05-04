package be.ugent.zeus.hydra.urgent.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import java9.util.function.Consumer;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.urgent.ProgrammeInformation;
import be.ugent.zeus.hydra.urgent.UrgentInfo;
import be.ugent.zeus.hydra.urgent.UrgentInfoRequest;
import okhttp3.Response;

/**
 * @author Niko Strijbol
 */
public class UrgentTrackProvider {

    static final String URGENT_ID = "be.ugent.zeus.hydra.urgent";

    public static final String METADATA_DESCRIPTION = "meta_description";

    private MediaMetadataCompat track;
    private final Context context;

    UrgentTrackProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    @SuppressLint("StaticFieldLeak")
    public void prepareMedia(@NonNull Consumer<MediaMetadataCompat> callback) {

        if (hasTrackInformation()) {
            callback.accept(track);
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
                callback.accept(track);
            }
        }.execute();
    }

    public boolean hasTrackInformation() {
        return track != null;
    }

    @SuppressLint("WrongConstant")
    private synchronized void loadData() {
        Request<UrgentInfo> infoRequest = new UrgentInfoRequest(context);
        Result<UrgentInfo> programme = infoRequest.execute();

        if (!programme.hasData()) {
            // It failed.
            return;
        }
        UrgentInfo info = programme.getData();

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, URGENT_ID)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, info.getUrl());

        ProgrammeInformation information = info.getMeta();

        if (!TextUtils.isEmpty(information.getName())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, information.getName())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, context.getString(R.string.urgent_fm));
        } else {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, context.getString(R.string.urgent_fm));
        }

        try {
            if (!TextUtils.isEmpty(information.getImageUrl())) {
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(information.getImageUrl())
                        .build();
                Response response = InstanceProvider.getClient(context).newCall(request).execute();
                assert response.body() != null;
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
            } else {
                Bitmap albumArt = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_urgent);
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
            }
        } catch (IOException ignored) {
            Bitmap albumArt = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_urgent);
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        }
        if (!TextUtils.isEmpty(information.getDescription())) {
            builder.putString(METADATA_DESCRIPTION, information.getDescription());
        }

        track = builder.build();
    }
}
