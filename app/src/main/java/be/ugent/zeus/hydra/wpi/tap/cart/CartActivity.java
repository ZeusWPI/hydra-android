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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.math.BigDecimal;
import java.text.NumberFormat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.EventObserver;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.barcode.Manager;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.scanner.BarcodeScanner;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.databinding.ActivityWpiTapCartBinding;
import be.ugent.zeus.hydra.wpi.tap.product.Product;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.wpi.tap.product.ProductFragment.FAVOURITE_PINNED_SHORTCUT;

/**
 * The Tap cart.
 *
 * @author Niko Strijbol
 */
public class CartActivity extends BaseActivity<ActivityWpiTapCartBinding> implements CartInteraction {

    private static final String TAG = "FormActivity";
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    public static final String ARG_FAVOURITE_PRODUCT_ID = "arg_favourite";
    public static final String ARG_FROM_SHORTCUT = "arg_from_shortcut";

    /**
     * The latest instance of the cart we've found.
     * TODO: this can probably be nicer by moving this to the view holder.
     */
    private CartViewModel viewModel;
    // Ugly hack to disable menus while submitting carts.
    private Boolean lastEnabledBoolean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiTapCartBinding::inflate);

        int initialProductId = -1;
        // If the saved instance is null, add the favourite product.
        // If the instance is not null, the cart will be restored, and the favourite
        // product will already be in the cart, so don't add it again.
        if (savedInstanceState == null) {
            initialProductId = getIntent().getIntExtra(ARG_FAVOURITE_PRODUCT_ID, -1);

            // Record use of the shortcut.
            if (getIntent().getBooleanExtra(ARG_FROM_SHORTCUT, false)) {
                ShortcutManagerCompat.reportShortcutUsed(this, FAVOURITE_PINNED_SHORTCUT);
            }
        }

        viewModel = new ViewModelProvider(this, new CartViewModel.Factory(getApplication(), initialProductId)).get(CartViewModel.class);
        updateCartSummary(null);

        binding.scanAdd.setOnClickListener(v -> {
            BarcodeScanner scanner = Manager.getScanner();
            if (scanner.needsActivity()) {
                Intent intent = scanner.getActivityIntent(CartActivity.this);
                startActivityForResult(intent, scanner.getRequestCode());
            } else {
                scanner.getBarcode(CartActivity.this, this::onBarcodeScan, this::onError);
            }
        });
        binding.manualAdd.setOnClickListener(v -> {
            ProductPickerDialogFragment productPicker = new ProductPickerDialogFragment();
            productPicker.show(getSupportFragmentManager(), "productPick");
        });

        // This is one ugly hack :(
        binding.bottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int bottomSheetHeight = binding.bottomSheet.getHeight();
                RecyclerView rv = binding.recyclerView;
                rv.setPadding(rv.getPaddingLeft(), rv.getPaddingTop(), rv.getPaddingRight(), (int) (bottomSheetHeight * 1.4));
                binding.bottomSheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(this));
        CartProductAdapter adapter = new CartProductAdapter(this);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeColors(ColourUtils.resolveColour(this, R.attr.colorSecondary));

        viewModel.getData().observe(this, PartialErrorObserver.with(this::onCartLoadError));
        // A custom adapter to map the data and save an instance of it.
        viewModel.getData().observe(this, new SuccessObserver<Cart>() {
            @Override
            protected void onSuccess(@NonNull Cart data) {
                Log.i(TAG, "onSuccess: received cart, with X items: " + data.getOrders().size());
                adapter.submitData(data.getOrders());
                viewModel.registerLastCart(data);
                updateCartSummary(data);
            }
        });
        viewModel.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);

        viewModel.getRequestResult().observe(this, EventObserver.with(orderResult -> {
            if (orderResult.isWithoutError()) {
                BigDecimal total = orderResult.getData().getPrice();
                String formattedTotal = currencyFormatter.format(total);
                String message = getString(R.string.wpi_tap_order_ok, formattedTotal);
                Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                this.clearCart(true);
                finish();
            } else {
                RequestException e = orderResult.getError();
                Log.e(TAG, "error during transaction request", e);
                String message = e.getMessage();
                new MaterialAlertDialogBuilder(CartActivity.this)
                        .setTitle(android.R.string.dialog_alert_title)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setMessage(getString(R.string.wpi_tap_form_error) + "\n" + message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        }));

        viewModel.getNetworkState().observe(this, networkState -> {
            boolean enabled = networkState == null || networkState == NetworkState.IDLE;
            lastEnabledBoolean = enabled;
            binding.scanAdd.setEnabled(enabled);
            binding.cartPay.setEnabled(enabled);
            if (enabled) {
                binding.cartProgress.setVisibility(View.GONE);
            } else {
                binding.cartProgress.setVisibility(View.VISIBLE);
            }
            invalidateOptionsMenu();
        });

        binding.cartPay.setOnClickListener(v -> {
            if (viewModel.getLastCart() == null) {
                Toast.makeText(CartActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                return;
            }
            String formattedTotal = currencyFormatter.format(viewModel.getLastCart().getTotalPrice());
            String message = getString(R.string.wpi_tap_form_confirm, formattedTotal);
            new MaterialAlertDialogBuilder(CartActivity.this)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> viewModel.startRequest(viewModel.getLastCart()))
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });
    }

    private void onCartLoadError(Throwable throwable) {
        Log.e(TAG, "Error while getting cart data.", throwable);
        Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(binding.getRoot(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }

    private void onBarcodeScan(String barcode) {
        if (barcode == null) {
            return;
        }
        if (viewModel.getLastCart() == null) {
            // There is no cart yet.
            Log.w(TAG, "onCreate: cart not ready yet...");
            Snackbar.make(binding.getRoot(), getString(R.string.wpi_tap_too_quick), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        Product foundProduct = viewModel.getLastCart().getProductFor(barcode);
        if (foundProduct == null) {
            Log.w(TAG, "onCreate: barcode niet gevonden in map " + barcode);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, barcode);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);

            new MaterialAlertDialogBuilder(CartActivity.this)
                .setMessage(getString(R.string.wpi_tap_product_not_found, barcode))
                .setPositiveButton(R.string.action_share, (dialog, which) -> startActivity(shareIntent))
                .setNegativeButton(android.R.string.no, null)
                .show();
            return;
        }
        Cart newCart = viewModel.getLastCart().addProduct(foundProduct);
        saveCart(newCart, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Manager.getScanner().getRequestCode()) {
            // Handle it.
            String barcode = Manager.getScanner().interpretActivityResult(data, resultCode);
            onBarcodeScan(barcode);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateCartSummary(Cart cart) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalProducts = 0;
        if (cart != null) {
            totalAmount = cart.getTotalPrice();
            totalProducts = cart.getTotalProducts();
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
        if (viewModel.getLastCart() != null) {
            saveCart(viewModel.getLastCart(), true);
        }
    }

    private void saveCart(Cart toSave, boolean stopping) {
        StorageCart storage = toSave.forStorage();
        ExistingCartRequest.saveCartStorage(this, storage);
        viewModel.registerLastCart(toSave);
        if (!stopping) {
            viewModel.requestRefresh();
        }
    }

    @Override
    public void increment(CartProduct product) {
        if (this.viewModel.getLastCart() != null) {
            Cart newCart = this.viewModel.getLastCart().increment(product);
            saveCart(newCart, false);
        }
    }

    @Override
    public void decrement(CartProduct product) {
        if (this.viewModel.getLastCart() != null) {
            Cart newCart = this.viewModel.getLastCart().decrement(product);
            saveCart(newCart, false);
        }
    }

    @Override
    public void remove(CartProduct product) {
        if (this.viewModel.getLastCart() != null) {
            Cart newCart = this.viewModel.getLastCart().remove(product);
            saveCart(newCart, false);
        }
    }

    @Override
    public void add(Product product) {
        if (this.viewModel.getLastCart() != null) {
            Cart newCart = this.viewModel.getLastCart().addProduct(product);
            saveCart(newCart, false);
        }
    }

    private void clearCart(boolean stopping) {
        Cart newCart = this.viewModel.getLastCart();
        if (newCart == null) {
            // If closing too fast, this is still null.
            return;
        }
        newCart = newCart.clear();
        saveCart(newCart, stopping);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_cart_clear) {
            this.clearCart(false);
            return true;
        } else if (itemId == android.R.id.home) {
            // The user has explicitly pressed the "cross"/close button.
            // We assume they are done with the cart, so we clear it.
            // If pressing the back button, we don't clear it.
            this.clearCart(false);
            // Do not return, but continue with normal processing.
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_FAVOURITE_PRODUCT_ID, true);
    }
}
