/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.urgent.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.utils.ThreadingUtils;
import be.ugent.zeus.hydra.urgent.ProgrammeInformation;
import be.ugent.zeus.hydra.urgent.UrgentInfo;
import be.ugent.zeus.hydra.urgent.UrgentInfoRequest;
import okhttp3.Response;

/**
 * @author Niko Strijbol
 */
public class UrgentTrackProvider {

    public static final String METADATA_DESCRIPTION = "meta_description";
    static final String URGENT_ID = "be.ugent.zeus.hydra.urgent";
    private final Context context;
    private MediaMetadataCompat track;

    UrgentTrackProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    @SuppressLint("StaticFieldLeak")
    public void prepareMedia(@NonNull Consumer<MediaMetadataCompat> callback) {

        if (hasTrackInformation()) {
            callback.accept(track);
            return;
        }
        
        ThreadingUtils.execute(this::loadData, () -> callback.accept(track));
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
