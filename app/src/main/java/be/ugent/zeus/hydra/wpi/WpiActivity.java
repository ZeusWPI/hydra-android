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

package be.ugent.zeus.hydra.wpi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.OffsetDateTime;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.*;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.common.ui.recyclerview.headers.HeaderAdapter;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.databinding.ActivityWpiBinding;
import be.ugent.zeus.hydra.wpi.account.AccountManager;
import be.ugent.zeus.hydra.wpi.account.ApiKeyManagementActivity;
import be.ugent.zeus.hydra.wpi.cammie.CammieActivity;
import be.ugent.zeus.hydra.wpi.door.DoorRequest;
import be.ugent.zeus.hydra.wpi.door.DoorViewModel;
import be.ugent.zeus.hydra.wpi.tab.create.FormActivity;
import be.ugent.zeus.hydra.wpi.tab.list.TransactionAdapter;
import be.ugent.zeus.hydra.wpi.tab.requests.TabRequestsAdapter;
import be.ugent.zeus.hydra.wpi.tap.cart.CartActivity;
import be.ugent.zeus.hydra.wpi.tap.order.OrderAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

/**
 * Main activity for Zeus mode.
 * <p>
 * Take care to look at the {@link #onCreate(Bundle)} function, as this
 * activity sets up quite a few things that should go together.
 *
 * @author Niko Strijbol
 */
public class WpiActivity extends BaseActivity<ActivityWpiBinding> {

    private static final String TAG = "ApiKeyManagementActivit";
    public static final int ACTIVITY_DO_REFRESH = 963;

    private WpiViewModel viewModel;
    // null => no favourite, e.g. either not loaded or not available.
    private Integer favouriteProductId;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiBinding::inflate);

        // Attach listeners to FABs.
        binding.tabTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(WpiActivity.this, FormActivity.class);
            startActivityForResult(intent, ACTIVITY_DO_REFRESH);
        });
        binding.tapOrder.setOnClickListener(v -> {
            Intent intent = new Intent(WpiActivity.this, CartActivity.class);
            startActivityForResult(intent, ACTIVITY_DO_REFRESH);
        });
        binding.tapAddFavourite.setOnClickListener(v -> {
            if (favouriteProductId == null) {
                // There is no favourite product, so don't do anything.
                return;
            }
            Intent intent = new Intent(WpiActivity.this, CartActivity.class);
            intent.putExtra(CartActivity.ARG_FAVOURITE_PRODUCT_ID, favouriteProductId);
            startActivityForResult(intent, ACTIVITY_DO_REFRESH);
        });

        syncDoorButtons();

        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(WpiViewModel.class);
        viewModel.getUserData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getUserData().observe(this, SuccessObserver.with(user -> {
            String balance = currencyFormatter.format(user.getBalanceDecimal());
            binding.tabBalance.setText(balance);
            int colour;
            if (user.getBalanceDecimal().compareTo(BigDecimal.ONE) < 0) {
                colour = ColourUtils.resolveColour(this, R.attr.colorError);
            } else {
                colour = ColourUtils.resolveColour(this, R.attr.colorOnSurface);
            }
            favouriteProductId = user.getFavourite();
            binding.tapAddFavourite.setVisibility(favouriteProductId == null ? View.GONE : View.VISIBLE);
            binding.tabBalance.setTextColor(colour);
            syncDoorButtons();
        }));

        binding.openCammie.setOnClickListener(v -> {
            Intent intent = new Intent(WpiActivity.this, CammieActivity.class);
            startActivity(intent);
        });

        DoorViewModel doorViewModel = provider.get(DoorViewModel.class);
        doorViewModel.getNetworkState().observe(this, networkState -> {
            boolean enabled = networkState == null || networkState == NetworkState.IDLE;

            // If the buttons are currently disabled, and we want to re-enable them,
            // we add a delay of about 3 seconds.
            // This scenario is most likely after a button was pressed, but the HTTP call
            // is much faster than the actual lock.
            // In the other case, just do it.
            if (enabled && (!binding.doorOpen.isEnabled() || !binding.doorClose.isEnabled())) {
                new Handler().postDelayed(() -> {
                    binding.doorOpen.setEnabled(true);
                    binding.doorClose.setEnabled(true);
                }, 3000);
            } else {
                binding.doorOpen.setEnabled(enabled);
                binding.doorClose.setEnabled(enabled);
            }
        });

        binding.doorClose.setOnClickListener(v -> new MaterialAlertDialogBuilder(WpiActivity.this)
                .setMessage(R.string.wpi_door_close)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> doorViewModel.startRequest(DoorRequest.Command.CLOSE))
                .setNegativeButton(android.R.string.cancel, null)
                .show());
        binding.doorOpen.setOnClickListener(v -> new MaterialAlertDialogBuilder(WpiActivity.this)
                .setMessage(R.string.wpi_door_open)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> doorViewModel.startRequest(DoorRequest.Command.OPEN))
                .setNegativeButton(android.R.string.cancel, null)
                .show());

        // Set up the adapters.
        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder()
                .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
                .build();
        ConcatAdapter mainAdapter = new ConcatAdapter(config);

        TabRequestsAdapter tabRequestsAdapter = new TabRequestsAdapter(
                tabRequest -> new MaterialAlertDialogBuilder(WpiActivity.this)
                        .setMessage(R.string.wpi_tab_request_accept_description)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> viewModel.acceptRequest(tabRequest))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show(),
                tabRequest -> new MaterialAlertDialogBuilder(WpiActivity.this)
                        .setMessage(R.string.wpi_tab_request_decline_description)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> viewModel.declineRequest(tabRequest))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
        );
        RecyclerView.Adapter<?> finalRequestAdapter = HeaderAdapter.makeHideable(R.string.wpi_tab_requests, tabRequestsAdapter);

        OrderAdapter tapOrderAdapter = new OrderAdapter(order -> {
            OffsetDateTime now = OffsetDateTime.now();
            // We can no longer cancel the order...
            if (now.isAfter(order.getDeletableUntil())) {
                Toast.makeText(this, R.string.wpi_tap_order_too_late, Toast.LENGTH_SHORT)
                        .show();
                viewModel.onRefresh();
            } else {
                new MaterialAlertDialogBuilder(WpiActivity.this)
                        .setMessage(R.string.wpi_tap_order_cancel_hint)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> viewModel.cancelOrder(order))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        RecyclerView.Adapter<?> finalOrderAdapter = HeaderAdapter.makeHideable(R.string.wpi_tap_pending_order, tapOrderAdapter);

        TransactionAdapter transactionAdapter = new TransactionAdapter();
        RecyclerView.Adapter<?> finalTransactionAdapter = HeaderAdapter.makeHideable(R.string.wpi_tab_transactions, transactionAdapter);

        mainAdapter.addAdapter(finalRequestAdapter);
        mainAdapter.addAdapter(finalOrderAdapter);
        mainAdapter.addAdapter(finalTransactionAdapter);

        // Show recycler view.
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(new SpanItemSpacingDecoration(this));
        binding.recyclerView.setAdapter(mainAdapter);

        viewModel.getRefreshing().observe(this, binding.swipeRefreshLayout::setRefreshing);

        // Attach request data.
        viewModel.getRequestData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getRequestData().observe(this, new ProgressObserver<>(binding.progressBar));
        viewModel.getRequestData().observe(this, new AdapterObserver<>(tabRequestsAdapter));

        // Attach pending order data.
        viewModel.getOrderData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getOrderData().observe(this, new ProgressObserver<>(binding.progressBar));
        viewModel.getOrderData().observe(this, new AdapterObserver<>(tapOrderAdapter));

        // Attach transaction data.
        viewModel.getTransactionData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getTransactionData().observe(this, new ProgressObserver<>(binding.progressBar));
        viewModel.getTransactionData().observe(this, new AdapterObserver<>(transactionAdapter));

        // Listen to network updates.
        viewModel.getNetworkState().observe(this, networkState ->
                binding.progressBar.setEnabled(networkState == null || networkState == NetworkState.IDLE));

        // Listen for updates on cancelled orders.
        viewModel.getOrderRequestResult().observe(this, EventObserver.with(booleanResult -> {
            if (booleanResult.isWithoutError()) {
                Toast.makeText(WpiActivity.this, R.string.wpi_tap_order_cancelled, Toast.LENGTH_SHORT).show();
            } else {
                onError(booleanResult.getError());
            }
            viewModel.onRefresh();
        }));

        viewModel.getActionRequestResult().observe(this, EventObserver.with(booleanResult -> {
            if (booleanResult.isWithoutError()) {
                Toast.makeText(WpiActivity.this, R.string.wpi_tab_request_executed, Toast.LENGTH_SHORT).show();
            } else {
                onError(booleanResult.getError());
            }
            viewModel.onRefresh();
        }));

        // Fix color of swipe refresh layout.
        binding.swipeRefreshLayout.setColorSchemeColors(ColourUtils.resolveColour(this, R.attr.colorSecondary));
        binding.swipeRefreshLayout.setOnRefreshListener(viewModel);
    }

    private void syncDoorButtons() {
        boolean hasDoorKey = !TextUtils.isEmpty(AccountManager.getDoorKey(this));
        binding.doorOpen.setVisibility(hasDoorKey ? View.VISIBLE : View.GONE);
        binding.doorClose.setVisibility(hasDoorKey ? View.VISIBLE : View.GONE);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        // TODO: better error message.
        Snackbar.make(binding.getRoot(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wpi, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_manage_login) {
            Intent intent = new Intent(this, ApiKeyManagementActivity.class);
            startActivityForResult(intent, ACTIVITY_DO_REFRESH);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult: result");

        if (requestCode == ACTIVITY_DO_REFRESH && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "onActivityResult: refreshing for result...");
            viewModel.onRefresh();
        }
    }
}
