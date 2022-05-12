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

package be.ugent.zeus.hydra.wpi.tap.cart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.NumberFormat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.barcode.Manager;
import be.ugent.zeus.hydra.common.scanner.BarcodeScanner;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.databinding.ActivityWpiTapCartBinding;
import com.google.android.material.snackbar.Snackbar;

/**
 * The Tap cart.
 *
 * @author Niko Strijbol
 */
public class CartActivity extends BaseActivity<ActivityWpiTapCartBinding> {

    private static final String TAG = "FormActivity";
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    private Cart lastCart;
    private CartViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiTapCartBinding::inflate);
        
        BarcodeScanner scanner = Manager.getScanner();
        scanner.getBarcodes(this);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(this));
        CartProductAdapter adapter = new CartProductAdapter();
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeColors(ColourUtils.resolveColour(this, R.attr.colorSecondary));

        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        // A custom adapter to map the data and save an instance of it.
        viewModel.getData().observe(this, new SuccessObserver<Cart>() {
            @Override
            protected void onSuccess(@NonNull Cart data) {
                adapter.submitData(data.getOrders());
                lastCart = data;
            }

            @Override
            protected void onEmpty() {
                adapter.clear();
            }
        });
        viewModel.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);

//        model.getRequestResult().observe(this, EventObserver.with(booleanResult -> {
//            if (booleanResult.isWithoutError()) {
//                int string;
//                if (formObject.getAmount() < 0) {
//                    string = R.string.wpi_tab_form_done_negative;
//                } else {
//                    string = R.string.wpi_tab_form_done;
//                }
//                Toast.makeText(CartActivity.this, string, Toast.LENGTH_SHORT).show();
//                setResult(RESULT_OK);
//                finish();
//            } else {
//                RequestException e = booleanResult.getError();
//                Log.e(TAG, "error during transaction request", e);
//                if (e instanceof TabRequestException) {
//                    List<String> messages = ((TabRequestException) e).getMessages();
//                    handleErrorMessages(messages);
//                } else {
//                    String message = e.getMessage();
//                    new MaterialAlertDialogBuilder(CartActivity.this)
//                            .setTitle(android.R.string.dialog_alert_title)
//                            .setIconAttribute(android.R.attr.alertDialogIcon)
//                            .setMessage(getString(R.string.wpi_tab_form_error) + "\n" + message)
//                            .setPositiveButton(android.R.string.ok, null)
//                            .show();
//                }
//            }
//        }));

//        model.getNetworkState().observe(this, networkState -> {
//            boolean enabled = networkState == null || networkState == NetworkState.IDLE;
//            binding.formMember.setEnabled(enabled);
//            binding.formAmount.setEnabled(enabled);
//            binding.formMessage.setEnabled(enabled);
//            binding.confirmButton.setEnabled(enabled);
//            if (networkState == NetworkState.BUSY) {
//                binding.formAmountLayout.setError(null);
//                binding.formMemberLayout.setError(null);
//            }
//        });

        // Set up sync between UI and object.
//        binding.formAmount.addTextChangedListener((SimpleTextWatcher) newText -> {
//            if (TextUtils.isEmpty(newText)) {
//                return;
//            }
//            try {
//                new BigDecimal(newText);
//                binding.formAmount.setError(null);
//            } catch (NumberFormatException e) {
//                Log.e(TAG, "onCreate: ", e);
//                binding.formAmount.setError("Wrong format");
//            }
//        });
        
//        binding.confirmButton.setOnClickListener(v -> {
//            syncData();
//            String message;
//            if (formObject.getAmount() < 0) {
//                message = getString(R.string.wpi_tab_form_confirm_negative, 
//                        currencyFormatter.format(formObject.getAdjustedAmount().negate()),
//                        formObject.getDestination());
//            } else {
//                message = getString(R.string.wpi_tab_form_confirm_positive, 
//                        currencyFormatter.format(formObject.getAdjustedAmount()),
//                        formObject.getDestination());
//            }
//            new MaterialAlertDialogBuilder(CartActivity.this)
//                    .setMessage(message)
//                    .setPositiveButton(android.R.string.ok, (dialog, which) -> model.startRequest(formObject))
//                    .setNegativeButton(android.R.string.cancel, null)
//                    .show();
//        });
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(binding.getRoot(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveCart(true);
    }
    
    private void saveCart(boolean stopping) {
        if (this.lastCart != null) {
            CartStorage storage = this.lastCart.asStorage();
            ExistingCartRequest.saveCartStorage(this, storage);
            if (!stopping) {
                viewModel.requestRefresh();
            }
        }
    }
}
