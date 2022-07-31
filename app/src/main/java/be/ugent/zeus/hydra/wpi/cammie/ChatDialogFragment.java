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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.DialogCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.NetworkState;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A dialog fragment allowing the user to search for and pick a product.
 *
 * @author Niko Strijbol
 */
public class ChatDialogFragment extends DialogFragment {

    private CammieViewModel vm;
    private boolean buttonPressed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(this).get(CammieViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputLayout textInputLayout = ViewCompat.requireViewById(view, R.id.message_layout);
        // Attach the network listener.
        vm.getMessageNetworkState().observe(this, networkState -> {
            boolean enabled = networkState == null || networkState == NetworkState.IDLE;

            if (enabled) {
                // The network is back to idle, so we are done.
                dismiss();
            } else {
                textInputLayout.setEnabled(false);
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog ourDialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.wpi_cammie_chat_dialog_title)
                .setView(R.layout.fragment_wpi_cammie_chat)
                .setPositiveButton(R.string.action_send, (dialog, which) -> {
                    buttonPressed = true;
                    TextInputEditText input = (TextInputEditText) DialogCompat.requireViewById((Dialog) dialog, R.id.message_entry);
                    vm.sendMessage(input.getEditableText().toString());
                })
                .create();
        ourDialog.setOnShowListener(dialog -> {
            // Show the keyboard and stuff by default.
            View input = DialogCompat.requireViewById((Dialog) dialog, R.id.message_entry);
            input.requestFocus();
        });
        // Ensure keyboard is shown.
        ourDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return ourDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (buttonPressed) {
            Toast.makeText(requireActivity(), R.string.wpi_cammie_chat_sent, Toast.LENGTH_LONG).show();
        }
        super.onDismiss(dialog);
    }
}
