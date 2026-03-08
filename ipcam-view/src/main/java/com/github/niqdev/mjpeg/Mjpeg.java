package com.github.niqdev.mjpeg;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A library wrapper for handle mjpeg streams.
 *
 * <li><a href="https://bitbucket.org/neuralassembly/simplemjpegview">simplemjpegview</a></li>
 * <li><a href="https://code.google.com/archive/p/android-camera-axis">android-camera-axis</a></li>
 * </ul>
 */
public class Mjpeg {
    private static final String TAG = Mjpeg.class.getSimpleName();

    public static Mjpeg newInstance() {
        return new Mjpeg();
    }

    @NonNull
    private Observable<MjpegInputStream> connect(String url) {
        return Observable.defer(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                loadConnectionProperties(urlConnection);
                InputStream inputStream = urlConnection.getInputStream();
                return Observable.just(new MjpegInputStream(inputStream));
            } catch (IOException e) {
                Log.e(TAG, "error during connection", e);
                return Observable.error(e);
            }
        });
    }

    /**
     * Connect to a Mjpeg stream.
     *
     * @param url source
     * @return Observable Mjpeg stream
     */
    public Observable<MjpegInputStream> open(String url) {
        return connect(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Connect to a Mjpeg stream.
     *
     * @param url     source
     * @param timeout in seconds
     * @return Observable Mjpeg stream
     */
    public Observable<MjpegInputStream> open(String url, int timeout) {
        return connect(url)
                .timeout(timeout, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Configure request properties
     *
     * @param urlConnection the url connection to add properties and cookies to
     */
    private void loadConnectionProperties(HttpURLConnection urlConnection) {
        urlConnection.setRequestProperty("Cache-Control", "no-cache");
    }
}
