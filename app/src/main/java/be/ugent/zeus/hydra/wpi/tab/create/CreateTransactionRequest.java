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

package be.ugent.zeus.hydra.wpi.tab.create;

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
public class CreateTransactionRequest extends OkHttpRequest<Boolean> {

    private final TransactionForm form;
    private final Context context;

    public CreateTransactionRequest(@NonNull Context context, @NonNull TransactionForm form) {
        super(context);
        this.context = context.getApplicationContext();
        this.form = form;
    }
    
    @NonNull
    @Override
    @WorkerThread
    public Result<Boolean> execute(@NonNull Bundle args) {

        MediaType json = MediaType.get("application/json; charset=utf-8");

        Map<String, Map<String, Object>> data = new HashMap<>();
        data.put("transaction", form.asApiObject(AccountManager.getUsername(context)));
        Type type = Types.newParameterizedType(Map.class, String.class, Types.newParameterizedType(Map.class, String.class, Object.class));
        JsonAdapter<Map<String, Map<String, Object>>> adapter = moshi.adapter(type);

        String rawData = adapter.toJson(data);
        
        // Create a request body.
        RequestBody body = RequestBody.create(rawData, json);

        // Create the request itself.
        okhttp3.Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", json.toString())
                .addHeader("Authorization", "Bearer " + AccountManager.getTabKey(context))
                .url(Endpoints.TAB + "transactions")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return Result.Builder.fromData(true);
            } else if (response.code() == 422) {
                // If the body is null, this is unexpected.
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("Unexpected null body for response 422");
                }
                // Unprocessable entity
                Type errorType = Types.newParameterizedType(List.class, String.class);
                JsonAdapter<List<String>> errorAdapter = moshi.adapter(errorType);
                List<String> errors = errorAdapter.fromJson(responseBody.source());
                return Result.Builder.fromException(new TabRequestException(errors));
            } else {
                throw new IOException("Unexpected state in request; neither successful nor 422: got " + response.code());
            }
        } catch (JsonDataException | NullPointerException e) {
            // Create, log and throw exception, since this is not normal.
            String message = "The server did not respond with the expected format when creating a Tab transaction.";
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
