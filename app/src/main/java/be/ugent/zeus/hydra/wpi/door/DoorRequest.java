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

package be.ugent.zeus.hydra.wpi.door;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.InvalidFormatException;
import be.ugent.zeus.hydra.common.network.OkHttpRequest;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.wpi.account.AccountManager;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import okhttp3.*;

/**
 * Send a request to the door.
 *
 * @author Niko Strijbol
 */
public class DoorRequest extends OkHttpRequest<DoorRequestResult> {

    private static final String ENDPOINT = "/api/door/%s/%s";
    private static final RequestBody EMPTY_REQUEST = RequestBody.create(new byte[]{});

    public enum Command {
        OPEN("open"), CLOSE("lock"), STATUS("status");

        final String command;

        Command(String value) {
            this.command = value;
        }

        public static Command fromStringValue(String value) {
            return Command.valueOf(value.toUpperCase(Locale.ROOT));
        }
    }

    private final Context context;
    private final Command command;

    protected DoorRequest(@NonNull Context context, @NonNull Command command) {
        super(context);
        this.context = context;
        this.command = command;
    }

    @NonNull
    @Override
    public Result<DoorRequestResult> execute(@NonNull Bundle args) {
        String key = AccountManager.getDoorKey(context);
        String url = Endpoints.MATTERMORE + String.format(ENDPOINT, key, command.command);

        okhttp3.Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .url(url)
                .post(EMPTY_REQUEST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // If the body is null, this is unexpected.
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("Unexpected null body for response 200");
                }
                JsonAdapter<DoorRequestResult> adapter = moshi.adapter(DoorRequestResult.class);
                DoorRequestResult result = adapter.fromJson(responseBody.source());
                return Result.Builder.fromData(Objects.requireNonNull(result));
            } else if (response.code() == 400) {
                return Result.Builder.fromException(new RequestException("Command " + this.command.command + " was not in (open,lock,status)"));
            } else if (response.code() == 401) {
                return Result.Builder.fromException(new RequestException("Authorization error, check the API key."));
            } else {
                throw new IOException("Unexpected state in request; not 200/400/401: got " + response.code());
            }
        } catch (JsonDataException | NullPointerException e) {
            // Create, log and throw exception, since this is not normal.
            String message = "Unexpected response for door request.";
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
