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
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.Tracker;
import be.ugent.zeus.hydra.common.request.Request;
import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;

/**
 * Common implementation for requests using OkHttp.
 *
 * @author Niko Strijbol
 */
public abstract class OkHttpRequest<D> implements Request<D> {

    protected final Moshi moshi;
    protected final OkHttpClient client;
    protected final Tracker tracker;

    /**
     * Construct a new request. As this constructor is not type-safe, it must only be used internally.
     *
     * @param context The context.
     */
    protected OkHttpRequest(@NonNull Context context) {
        this.moshi = InstanceProvider.moshi();
        this.client = InstanceProvider.client(context);
        this.tracker = Reporting.getTracker(context);
    }
}
