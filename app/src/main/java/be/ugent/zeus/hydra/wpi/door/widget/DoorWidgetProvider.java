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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.wpi.door.DoorRequest;

import static be.ugent.zeus.hydra.wpi.door.widget.DoorRequestExecutor.KEY_COMMAND_STRING;

/**
 * Handle the widget for the door. The widget is relatively simple, since it only has two buttons.
 *
 * @author Niko Strijbol
 */
public class DoorWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "DoorWidgetProvider";

    private static final int COMMAND_OPEN_REQUEST_CODE = 963;
    private static final int COMMAND_CLOSE_REQUEST_CODE = 964;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: got update");
        for (int appWidgetId : appWidgetIds) {
            Log.d(TAG, "onUpdate: ID Is " + appWidgetId);
            PendingIntent openPendingIntent = getPendingIntent(context, DoorRequest.Command.OPEN, COMMAND_OPEN_REQUEST_CODE);
            PendingIntent closePendingIntent = getPendingIntent(context, DoorRequest.Command.CLOSE, COMMAND_CLOSE_REQUEST_CODE);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_door_button);
            views.setOnClickPendingIntent(R.id.close_button, closePendingIntent);
            views.setOnClickPendingIntent(R.id.open_button, openPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private PendingIntent getPendingIntent(Context context, DoorRequest.Command command, int requestCode) {
        Log.d(TAG, "getPendingIntent: getting intent");
        Intent intent = new Intent(context, DoorRequestExecutor.class);
        intent.putExtra(KEY_COMMAND_STRING, command.command);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            @SuppressLint("UnspecifiedImmutableFlag")
            PendingIntent t = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntent = t;
        }

        return pendingIntent;
    }
}
