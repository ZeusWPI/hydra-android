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

package be.ugent.zeus.hydra.wpi.tap.cart;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.InvalidFormatException;
import be.ugent.zeus.hydra.common.network.OkHttpRequest;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.wpi.account.AccountManager;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Types;
import okhttp3.*;

/**
 * Creates a new transaction.
 *
 * @author Niko Strijbol
 */
class CreateOrderRequest extends OkHttpRequest<OrderResult> {

    private final Cart cart;
    private final Context context;

    public CreateOrderRequest(@NonNull Context context, @NonNull Cart cart) {
        super(context);
        this.context = context.getApplicationContext();
        this.cart = cart;
    }

    @NonNull
    @Override
    @WorkerThread
    public Result<OrderResult> execute(@NonNull Bundle args) {

        MediaType json = MediaType.get("application/json; charset=utf-8");

        Map<String, Map<String, List<Map<String, Object>>>> data = new HashMap<>();
        data.put("order", cart.forJson());
        Type type = Types.newParameterizedType(Map.class, String.class, Types.newParameterizedType(Map.class, String.class,
                Types.newParameterizedType(List.class, Types.newParameterizedType(Map.class, String.class, Object.class)))
        );
        JsonAdapter<Map<String, Map<String, List<Map<String, Object>>>>> adapter = moshi.adapter(type);

        String rawData = adapter.toJson(data);

        // Create a request body.
        RequestBody body = RequestBody.create(rawData, json);

        // Create the request itself.
        Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", json.toString())
                .addHeader("Authorization", "Bearer " + AccountManager.getTapKey(context))
                .url(Endpoints.TAP + "users/" + AccountManager.getUsername(context) + "/orders")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // If the body is null, this is unexpected.
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("Unexpected null body for response");
                }
                JsonAdapter<OrderResult> resultAdapter = moshi.adapter(OrderResult.class);
                OrderResult result = resultAdapter.fromJson(responseBody.source());
                if (result == null || result.getId() == null) {
                    return Result.Builder.fromException(new RequestException("Unsuccessful transaction."));
                }
                return Result.Builder.fromData(result);
            } else {
                // TODO: unsufficient money is also 403, so handle that better in the activity.
                throw new IOException("Unexpected state in request; not successful: got " + response.code());
            }
        } catch (JsonDataException | NullPointerException e) {
            // Create, log and throw exception, since this is not normal.
            String message = "The server did not respond with the expected format when creating a Tap order.";
            InvalidFormatException exception = new InvalidFormatException(message, e);
            tracker.logError(exception);
            return Result.Builder.fromException(exception);
        } catch (IOException e) {
            // This is an unknown exception, e.g. the network is gone.
            RequestException exception = new RequestException(e);
            tracker.logError(exception);
            return Result.Builder.fromException(exception);
        }
    }
}
