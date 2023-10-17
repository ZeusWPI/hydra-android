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
import android.util.Log;
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
 * Send a request to move cammie around.
 *
 * @author Niko Strijbol
 */
public class MoveRequest extends OkHttpRequest<String> {

    private static final String TAG = "MoveRequest";
    private static final String ENDPOINT = "webcam/cgi/ptdc.cgi";
    private static final String REL = "set_relative_pos";
    private static final String ABS = "set_pos";

    public enum Command {
        NORTH_WEST(REL, -10, 10),
        NORTH(REL, 0, 10),
        NORTH_EAST(REL, 10, 10),
        WEST(REL, -10, 0),
        EAST(REL, 10, 0),
        SOUTH_WEST(REL, -10, -10),
        SOUTH(REL, 0, -10),
        SOUTH_EAST(REL, 10, -10),
        SMALL_TABLE(ABS, 22, 12),
        BIG_TABLE(ABS, 61, 10),
        SOFA(ABS, 56, 24),
        DOOR(ABS, 30, 4);

        final String command;
        final int x;
        final int y;

        Command(String command, int x, int y) {
            this.command = command;
            this.x = x;
            this.y = y;
        }
    }

    private final Command command;

    protected MoveRequest(@NonNull Context context, @NonNull Command command) {
        super(context);
        this.command = command;
    }

    @NonNull
    @Override
    public Result<String> execute(@NonNull Bundle args) {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(Endpoints.KELDER))
                .newBuilder()
                .addPathSegments(ENDPOINT)
                .addQueryParameter("command", command.command)
                .addQueryParameter("posX", String.valueOf(command.x))
                .addQueryParameter("posY", String.valueOf(command.y))
                .build();

        Log.d(TAG, "execute: doing door request to: " + url.toString());

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // If the body is null, this is unexpected.
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("Unexpected null body for response 200");
                }
                return Result.Builder.fromData(responseBody.string());
            } else {
                throw new IOException("Unexpected error, " + response.code());
            }
        } catch (NullPointerException e) {
            // Create, log and throw exception, since this is not normal.
            String message = "Unexpected response for cammie request.";
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
