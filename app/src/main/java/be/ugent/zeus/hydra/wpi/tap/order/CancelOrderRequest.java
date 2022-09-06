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

package be.ugent.zeus.hydra.wpi.tap.order;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.IOException;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.OkHttpRequest;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.wpi.account.AccountManager;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Cancel an order.
 *
 * @author Niko Strijbol
 */
public class CancelOrderRequest extends OkHttpRequest<Boolean> {

    private final Order order;
    private final Context context;

    public CancelOrderRequest(@NonNull Context context, @NonNull Order order) {
        super(context);
        this.context = context.getApplicationContext();
        this.order = order;
    }

    @NonNull
    @Override
    @WorkerThread
    public Result<Boolean> execute(@NonNull Bundle args) {

        MediaType json = MediaType.get("application/json; charset=utf-8");
        // Create the request itself.
        Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", json.toString())
                .addHeader("Authorization", "Bearer " + AccountManager.getTapKey(context))
                .url(Endpoints.TAP + "users/" + AccountManager.getUsername(context) + "/orders/" + order.getId())
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return Result.Builder.fromData(true);
            } else {
                throw new IOException("Something went wrong while cancelling the order. Response code " + response.code());
            }
        } catch (IOException e) {
            // This is an unknown exception, e.g. the network is gone.
            RequestException exception = new RequestException(e);
            tracker.logError(exception);
            return Result.Builder.fromException(exception);
        }
    }
}
