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

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.math.BigDecimal;
import java.text.NumberFormat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.barcode.Manager;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.scanner.BarcodeScanner;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.ActivityWpiTapCartBinding;
import be.ugent.zeus.hydra.wpi.tap.product.Product;
import com.google.android.material.snackbar.Snackbar;

/**
 * The Tap cart.
 *
 * @author Niko Strijbol
 */
public class CartActivity extends BaseActivity<ActivityWpiTapCartBinding> implements CartInteraction {

    private static final String TAG = "FormActivity";
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    /**
     * The latest instance of the cart we've found.
     * TODO: this can probably be nicer by moving this to the view holder.
     */
    private Cart lastCart;
    private CartViewModel viewModel;
    // Ugly hack to disable menus while submitting carts.
    private Boolean lastEnabledBoolean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiTapCartBinding::inflate);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        updateCartSummary(null);

        binding.scanAdd.setOnClickListener(v -> {
            BarcodeScanner scanner = Manager.getScanner();
            scanner.getBarcode(CartActivity.this, s -> {
                if (lastCart == null) {
                    // There is no cart yet.
                    Log.w(TAG, "onCreate: cart not ready yet...");
                    Snackbar.make(binding.getRoot(), "Product niet gevonden.", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                Product foundProduct = lastCart.getProductFor(s);
                if (foundProduct == null) {
                    Log.w(TAG, "onCreate: barcode niet gevonden in map " + s);
                    Snackbar.make(binding.getRoot(), "Product niet gevonden.", Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                Cart newCart = lastCart.addProduct(foundProduct);
                saveCart(newCart, false);
            }, this::onError);
        });

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(this));
        CartProductAdapter adapter = new CartProductAdapter(this);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeColors(ColourUtils.resolveColour(this, R.attr.colorSecondary));

        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        // A custom adapter to map the data and save an instance of it.
        viewModel.getData().observe(this, new SuccessObserver<Cart>() {
            @Override
            protected void onSuccess(@NonNull Cart data) {
                Log.i(TAG, "onSuccess: received cart, with X items: " + data.getOrders().size());
                adapter.submitData(data.getOrders());
                lastCart = data;
                updateCartSummary(data);
            }
        });
        viewModel.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);

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

        viewModel.getNetworkState().observe(this, networkState -> {
            boolean enabled = networkState == null || networkState == NetworkState.IDLE;
            lastEnabledBoolean = enabled;
            binding.scanAdd.setEnabled(enabled);
            binding.cartPay.setEnabled(enabled);
            invalidateOptionsMenu();
//            if (networkState == NetworkState.BUSY) {
//                binding.formAmountLayout.setError(null);
//                binding.formMemberLayout.setError(null);
//            }
        });

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
    
    private void updateCartSummary(Cart cart) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalProducts = 0;
        if (cart != null) {
            for (CartProduct product : cart.getOrders()) {
                totalAmount = totalAmount.add(product.getPriceDecimal().multiply(BigDecimal.valueOf(product.getAmount())));
                totalProducts += product.getAmount();
            }
        }
        
        // Set texts
        String totalString = getString(R.string.wpi_cart_total_amount, currencyFormatter.format(totalAmount));
        binding.cartSummaryAmount.setText(totalString);
        String articleString = getResources().getQuantityString(R.plurals.wpi_cart_total_products, totalProducts, totalProducts);
        binding.cartSummaryArticles.setText(articleString);
        binding.cartPay.setEnabled(totalProducts != 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lastCart != null) {
            saveCart(lastCart, true);
        }
    }

    private void saveCart(Cart toSave, boolean stopping) {
        StorageCart storage = toSave.forStorage();
        ExistingCartRequest.saveCartStorage(this, storage);
        this.lastCart = toSave;
        if (!stopping) {
            viewModel.requestRefresh();
        }
    }

    @Override
    public void increment(CartProduct product) {
        if (this.lastCart != null) {
            Cart newCart = this.lastCart.increment(product);
            saveCart(newCart, false);
        }
    }

    @Override
    public void decrement(CartProduct product) {
        if (this.lastCart != null) {
            Cart newCart = this.lastCart.decrement(product);
            saveCart(newCart, false);
        }
    }

    @Override
    public void remove(CartProduct product) {
        if (this.lastCart != null) {
            Cart newCart = this.lastCart.remove(product);
            saveCart(newCart, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_cart_clear) {
            Cart newCart = this.lastCart.clear();
            saveCart(newCart, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);

        // We need to manually set the color of this Drawable for some reason.
        tintToolbarIcons(menu, R.id.menu_cart_clear);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_cart_clear);
        item.setEnabled(lastEnabledBoolean == null || lastEnabledBoolean);
        return super.onPrepareOptionsMenu(menu);
    }
}
