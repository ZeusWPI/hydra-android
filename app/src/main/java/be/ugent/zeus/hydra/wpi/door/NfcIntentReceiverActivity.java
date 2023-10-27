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

import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Window;
import androidx.annotation.Nullable;
import androidx.core.content.IntentCompat;
import androidx.lifecycle.ViewModelProvider;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityWpiDoorDialogBinding;

/**
 * Receives the intent, and starts a service.
 *
 * @author Niko Strijbol
 */
public class NfcIntentReceiverActivity extends BaseActivity<ActivityWpiDoorDialogBinding> {

    private static final String TAG = "NfcIntentReceiver";

    private DoorViewModel vm;
    private boolean wasRequestSent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(ActivityWpiDoorDialogBinding::inflate, false);

        Log.i(TAG, "onCreate: starting new request...");

        vm = new ViewModelProvider(this).get(DoorViewModel.class);
        vm.getNetworkState().observe(this, networkState -> {
            if (networkState == null || networkState == NetworkState.IDLE) {
                // The network is back to idle, so we are done.
                // TODO: show toast for errors maybe?
                if (wasRequestSent) {
                    finish();
                }
            } else {
                wasRequestSent = true;
            }
        });

        handleRequest(getIntent());
    }

    private void handleRequest(Intent intent) {
        if (!NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            return;
        }

        Parcelable[] rawMessages = IntentCompat.getParcelableArrayExtra(intent, NfcAdapter.EXTRA_NDEF_MESSAGES, Parcelable.class);
        // We only expect one message, so we only listen for one message.
        if (rawMessages == null || rawMessages.length != 1) {
            return;
        }

        NdefMessage message = (NdefMessage) rawMessages[0];
        NdefRecord[] records = message.getRecords();
        if (records.length != 1) {
            return;
        }

        NdefRecord record = records[0];
        Uri uri = record.toUri();
        String doorCommand = uri.getPath().substring(1);

        Log.i(TAG, "handleRequest: path from URI was " + doorCommand);

        binding.doorStatusText.setText(getString(R.string.wpi_door_sending_request, doorCommand));
        DoorRequest.Command command = DoorRequest.Command.fromStringValue(doorCommand);
        vm.startRequest(command);
    }
}
