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

package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import androidx.annotation.VisibleForTesting;

import java.io.File;

import be.ugent.zeus.hydra.common.converter.*;
import com.squareup.moshi.Moshi;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Provide instances of singletons. In the future this might be replaced with dependency injection.
 *
 * @author Niko Strijbol
 */
public final class InstanceProvider {

    private static final long CACHE_SIZE = 20 * 1024 * 1024; // 20 MiB
    private static OkHttpClient client;
    private static Moshi moshi;

    private InstanceProvider() {
    }

    private static synchronized OkHttpClient client(File cacheDir) {
        if (client == null) {
            client = builder(cacheDir).build();
        }
        return client;
    }

    @VisibleForTesting
    public static void client(OkHttpClient client) {
        InstanceProvider.client = client;
    }

    @VisibleForTesting
    public static OkHttpClient.Builder builder(File cacheDir) {
        //noinspection KotlinInternalInJava
        return new OkHttpClient.Builder().cache(new Cache(cacheDir, CACHE_SIZE));
    }

    /**
     * Get the OkHttpClient we use for requests.
     *
     * @param context A context.
     * @return The client.
     */
    public static synchronized OkHttpClient client(Context context) {
        File cacheDir = new File(context.getCacheDir(), "http");
        return client(cacheDir);
    }

    public static synchronized Moshi moshi() {
        if (moshi == null) {
            moshi = new Moshi.Builder()
                    .add(new BooleanJsonAdapter())
                    .add(new LocalDateAdapter())
                    .add(new DateTypeConverters.GsonOffset())
                    .add(new DateTypeConverters.LocalZonedDateTimeInstance())
                    .add(new PairJsonAdapter.Factory())
                    .build();
        }
        return moshi;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static void reset() {
        client = null;
        moshi = null;
    }
}