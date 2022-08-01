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

package be.ugent.zeus.hydra.wpi.cammie;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Objects;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.InvalidFormatException;
import be.ugent.zeus.hydra.common.network.OkHttpRequest;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import okhttp3.*;

/**
 * Request to send a message to Cammie Chat.
 * 
 * @author Niko Strijbol
 */
public class ChatRequest extends OkHttpRequest<String> {
    
    private static final String ENDPOINT = "messages/";
    
    private final String message;

    public ChatRequest(@NonNull Context context, @NonNull String message) {
        super(context);
        this.message = message;
    }

    @NonNull
    @Override
    public Result<String> execute(@NonNull Bundle args) {

        MediaType plainText = MediaType.get("plain/text");
        
        RequestBody body = RequestBody.create(message, plainText);

        // Create the request itself.
        okhttp3.Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", plainText.toString())
                .url(Endpoints.KELDER + ENDPOINT)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return Result.Builder.fromData(Objects.requireNonNull(response.body()).string());
            } else {
                throw new IOException("Unexpected response, got " + response.code());
            }
        } catch (NullPointerException e) {
            // Create, log and throw exception, since this is not normal.
            String message = "Did not get a body when sending a request.";
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
