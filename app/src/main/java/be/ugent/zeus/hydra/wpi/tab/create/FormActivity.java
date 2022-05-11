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

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.EventObserver;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.SimpleTextWatcher;
import be.ugent.zeus.hydra.databinding.ActivityWpiTabTransactionFormBinding;

/**
 * Form where the user can create a new transaction.
 *
 * @author Niko Strijbol
 */
public class FormActivity extends BaseActivity<ActivityWpiTabTransactionFormBinding> {

    private static final String TAG = "FormActivity";
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private static final String KEY_FORM_OBJECT = "wpi_tab_transaction_object";

    private TransactionForm formObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiTabTransactionFormBinding::inflate);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FORM_OBJECT)) {
            formObject = savedInstanceState.getParcelable(KEY_FORM_OBJECT);
        } else {
            formObject = new TransactionForm();
        }

        TransactionViewModel model = new ViewModelProvider(this).get(TransactionViewModel.class);

        model.getRequestResult().observe(this, EventObserver.with(booleanResult -> {
            if (booleanResult.isWithoutError()) {
                int string;
                if (formObject.getAmount() < 0) {
                    string = R.string.wpi_tab_form_done_negative;
                } else {
                    string = R.string.wpi_tab_form_done;
                }
                Toast.makeText(FormActivity.this, string, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                RequestException e = booleanResult.getError();
                Log.e(TAG, "error during transaction request", e);
                if (e instanceof TabRequestException) {
                    List<String> messages = ((TabRequestException) e).getMessages();
                    handleErrorMessages(messages);
                } else {
                    String message = e.getMessage();
                    new MaterialAlertDialogBuilder(FormActivity.this)
                            .setTitle(android.R.string.dialog_alert_title)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setMessage(getString(R.string.wpi_tab_form_error) + "\n" + message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            }
        }));

        model.getNetworkState().observe(this, networkState -> {
            boolean enabled = networkState == null || networkState == TransactionViewModel.NetworkState.IDLE;
            binding.formMember.setEnabled(enabled);
            binding.formAmount.setEnabled(enabled);
            binding.formMessage.setEnabled(enabled);
            binding.confirmButton.setEnabled(enabled);
            if (networkState == TransactionViewModel.NetworkState.BUSY) {
                binding.formAmountLayout.setError(null);
                binding.formMemberLayout.setError(null);
            }
        });

        // Set up sync between UI and object.
        binding.formAmount.addTextChangedListener((SimpleTextWatcher) newText -> {
            if (TextUtils.isEmpty(newText)) {
                return;
            }
            try {
                new BigDecimal(newText);
                binding.formAmount.setError(null);
            } catch (NumberFormatException e) {
                Log.e(TAG, "onCreate: ", e);
                binding.formAmount.setError("Wrong format");
            }
        });
        
        binding.confirmButton.setOnClickListener(v -> {
            syncData();
            String message;
            if (formObject.getAmount() < 0) {
                message = getString(R.string.wpi_tab_form_confirm_negative, 
                        currencyFormatter.format(formObject.getAdjustedAmount().negate()),
                        formObject.getDestination());
            } else {
                message = getString(R.string.wpi_tab_form_confirm_positive, 
                        currencyFormatter.format(formObject.getAdjustedAmount()),
                        formObject.getDestination());
            }
            new MaterialAlertDialogBuilder(FormActivity.this)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> model.startRequest(formObject))
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });
    }
    
    private void syncData() {
        if (binding.formMember.getText() != null) {
            formObject.setDestination(binding.formMember.getText().toString());
        }
        if (binding.formAmount.getText() != null) {
            BigDecimal number = new BigDecimal(binding.formAmount.getText().toString()).movePointRight(2);
            int cents = number.intValue();
            formObject.setAmount(cents);
        }
        if (binding.formMessage.getText() != null) {
            formObject.setDescription(binding.formMessage.getText().toString());
        }
    }

    private void handleErrorMessages(List<String> messages) {
        // TODO: clean this up.
        Log.e(TAG, "handleErrorMessages: found error messages");
        
        // Amount field.
        List<String> numberMessages = new ArrayList<>();
        if (messages.contains("Amount must be greater than 0")) {
            numberMessages.add(getString(R.string.wpi_tab_form_error_amount_zero));
        }
        List<String> otherNumbers = messages.stream()
                .filter(p -> !p.equals("Amount must be greater than 0") && p.toLowerCase(Locale.ENGLISH).contains("amount"))
                .collect(Collectors.toList());
        numberMessages.addAll(otherNumbers);
        String bigMessage = TextUtils.join("\n", numberMessages);
        binding.formAmountLayout.setError(bigMessage);

        // Destination field.
        List<String> userMessages = new ArrayList<>();
        if (messages.contains("Creditor can't be blank") || messages.contains("Debtor can't be blank")) {
            userMessages.add(getString(R.string.wpi_tab_form_error_creditor_blank));
        }
        List<String> otherUsers = messages.stream()
                .filter(p -> !p.equals("Creditor can't be blank") && !p.equals("Debtor can't be blank") 
                        && (p.toLowerCase(Locale.ENGLISH).contains("creditor") || p.toLowerCase(Locale.ENGLISH).contains("debtor")))
                .collect(Collectors.toList());
        userMessages.addAll(otherUsers);
        String bigUserMessage = TextUtils.join("\n", userMessages);
        binding.formMemberLayout.setError(bigUserMessage);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_FORM_OBJECT, formObject);
        super.onSaveInstanceState(outState);
    }
}
