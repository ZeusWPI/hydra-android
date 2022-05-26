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

package be.ugent.zeus.hydra.wpi.door.widget;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.NotificationCompat;
import androidx.work.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ChannelCreator;
import be.ugent.zeus.hydra.wpi.door.DoorRequest;
import be.ugent.zeus.hydra.wpi.door.DoorRequestResult;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Execute a door command and shows a toast if successful.
 *
 * @author Niko Strijbol
 */
public class DoorRequestWork extends Worker {

    private static final String TAG = "DoorRequestWork";

    public static final String KEY_REQUEST = "key_type";
    private static final int NOTIFICATION_ID = 96;

    public DoorRequestWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static OneTimeWorkRequest createWork(String command) {
        Data inputData = new Data.Builder()
                .putString(KEY_REQUEST, command)
                .build();

        return new OneTimeWorkRequest.Builder(DoorRequestWork.class)
                .setInputData(inputData)
                .setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)
                .build();
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: begin work...");
        String commandString = getInputData().getString(KEY_REQUEST);
        if (commandString == null) {
            // Ignore.
            Log.d(TAG, "doWork: INPUT IS NULL");
            return Result.failure();
        }
        DoorRequest.Command command = DoorRequest.Command.fromString(commandString);
        DoorRequest request = new DoorRequest(getApplicationContext(), command);
        be.ugent.zeus.hydra.common.request.Result<DoorRequestResult> result = request.execute();
        
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (result.isWithoutError()) {
                String before = result.getData().getBefore();
                String message = getApplicationContext().getString(R.string.wpi_door_widget_success, commandString, before);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                String message = getApplicationContext().getString(R.string.wpi_door_widget_error, commandString);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        return Result.success();
    }

    @NonNull
    @Override
    public ListenableFuture<ForegroundInfo> getForegroundInfoAsync() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            completer.set(createForegroundInfo());
            return "What does this do?";
        });
    }

    private ForegroundInfo createForegroundInfo() {
        Context context = getApplicationContext();
        String title = context.getString(R.string.wpi_door_channel_title);
        ChannelCreator.createWpiWidgetChannel(context);
        Notification notification = new NotificationCompat.Builder(context, ChannelCreator.WPI_WIDGET_CHANNEL)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_notification_urgent)
                .setOngoing(true)
                .build();

        return new ForegroundInfo(NOTIFICATION_ID, notification);
    }
}
