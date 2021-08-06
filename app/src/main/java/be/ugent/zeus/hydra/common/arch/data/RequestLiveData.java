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

package be.ugent.zeus.hydra.common.arch.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * Live data for a {@link Request}.
 *
 * @author Niko Strijbol
 */
public class RequestLiveData<M> extends BaseLiveData<Result<M>> {

    private final Request<M> request;
    private final Context applicationContext;

    public RequestLiveData(Context context, Request<M> request) {
        this(context, request, true);
    }

    public RequestLiveData(Context context, Request<M> request, boolean load) {
        this.applicationContext = context.getApplicationContext();
        this.request = request;
        if (load) {
            loadData();
        }
    }

    /**
     * Load the actual data.
     *
     * @param bundle The arguments for the request.
     */
    @Override
    @SuppressLint("StaticFieldLeak")
    protected void loadData(@NonNull Bundle bundle) {
        new AsyncTask<Void, Void, Result<M>>() {

            @Override
            protected Result<M> doInBackground(Void... voids) {
                return getRequest().execute(bundle);
            }

            @Override
            protected void onPostExecute(Result<M> m) {
                setValue(m);
            }
        }
                .execute();
    }

    protected Context getContext() {
        return applicationContext;
    }

    protected Request<M> getRequest() {
        return request;
    }
}