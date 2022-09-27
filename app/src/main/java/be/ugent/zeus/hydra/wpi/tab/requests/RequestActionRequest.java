/*
 * Copyright (c) 2022 Niko Strijbol
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

package be.ugent.zeus.hydra.wpi.tab.requests;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.IOException;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.OkHttpRequest;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.wpi.account.AccountManager;
import okhttp3.MediaType;
import okhttp3.Response;

/**
 * Do something with a Tab request.
 *
 * @author Niko Strijbol
 */
public class RequestActionRequest extends OkHttpRequest<Boolean> {

    private final Context context;
    private final String action;
    private final TabRequest request;

    /**
     * Construct a new request.
     *
     * @param context The context.
     */
    public RequestActionRequest(@NonNull Context context, @NonNull String action, @NonNull TabRequest request) {
        super(context);
        this.context = context.getApplicationContext();
        this.action = action;
        this.request = request;
    }

    @NonNull
    @Override
    @WorkerThread
    public Result<Boolean> execute(@NonNull Bundle args) {
        MediaType json = MediaType.get("application/json; charset=utf-8");
        // Create the request itself.
        okhttp3.Request request = new okhttp3.Request.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", json.toString())
                .addHeader("Authorization", "Bearer " + AccountManager.getTabKey(context))
                .url(Endpoints.TAB + "requests/" + this.request.getId() + "/" + this.action)
                .post(okhttp3.RequestBody.create("", null))
                .build();

        Log.d("TAG", request.toString());
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return Result.Builder.fromData(true);
            } else {
                throw new IOException("Something went wrong while applying the action to the request. Response code " + response.code());
            }
        } catch (IOException e) {
            // This is an unknown exception, e.g. the network is gone.
            RequestException exception = new RequestException(e);
            tracker.logError(exception);
            return Result.Builder.fromException(exception);
        }
    }
}
