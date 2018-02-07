package be.ugent.zeus.hydra.data.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Handle migration of accounts to new devices, using the Account Transfer API. This will receive the broadcast and
 * start a service responsible for handling everything.
 *
 * @author Niko Strijbol
 *
 * @see <a href="https://developer.android.com/guide/topics/data/account-transfer.html">The API Guide</a>
 */
public class AccountTransferReceiver extends BroadcastReceiver {

    private static final String TAG = "AccountTransferReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent: " + intent);

        // Start a foreground service to initiate the transfer.
        Intent serviceIntent = AccountTransferService.getIntent(context, intent.getAction());
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}