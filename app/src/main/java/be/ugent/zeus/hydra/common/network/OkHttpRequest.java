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
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.UnknownServiceException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.Tracker;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Common implementation for requests using OkHttp.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
public abstract class OkHttpRequest<D> implements Request<D> {

    private static final String TAG = "OkHttpRequest";
    
    protected final Moshi moshi;
    protected final OkHttpClient client;
    protected final Tracker tracker;

    /**
     * Construct a new request. As this constructor is not type-safe, it must only be used internally.
     *
     * @param context The context.
     */
    protected OkHttpRequest(@NonNull Context context) {
        this.moshi = InstanceProvider.getMoshi();
        this.client = InstanceProvider.getClient(context);
        this.tracker = Reporting.getTracker(context);
    }
}
