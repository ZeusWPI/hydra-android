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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.text.NumberFormat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityWpiBinding;
import be.ugent.zeus.hydra.wpi.account.AccountManager;
import be.ugent.zeus.hydra.wpi.account.ApiKeyManagementActivity;
import be.ugent.zeus.hydra.wpi.account.CombinedUserViewModel;
import be.ugent.zeus.hydra.wpi.door.DoorRequest;
import be.ugent.zeus.hydra.wpi.door.DoorViewModel;
import be.ugent.zeus.hydra.wpi.tab.create.FormActivity;
import be.ugent.zeus.hydra.wpi.tap.cart.CartActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

/**
 * Activity that allows you to manage your API key.
 * <p>
 * This is a temporary solution; at some point, we'll need to implement
 * a proper login solution (or do we? it is Zeus after all).
 *
 * @author Niko Strijbol
 */
public class WpiActivity extends BaseActivity<ActivityWpiBinding> {

    private static final String TAG = "ApiKeyManagementActivit";
    public static final int ACTIVITY_DO_REFRESH = 963;

    private CombinedUserViewModel combinedUserViewModel;
    private WpiPagerAdapter pageAdapter;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private final NumberFormat decimalFormatter = NumberFormat.getNumberInstance();

    private final ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                binding.tabFab.hide();
                binding.tapFab.show();
            } else if (position == 1) {
                binding.tapFab.hide();
                binding.tabFab.show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWpiBinding::inflate);
        setTitle();

        pageAdapter = new WpiPagerAdapter(this);
        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(pageAdapter);

        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.wpi_tap_tab);
            } else if (position == 1) {
                tab.setText(R.string.wpi_tab_tab);
            }
        });
        mediator.attach();

        binding.tabFab.setOnClickListener(v -> {
            Intent intent = new Intent(WpiActivity.this, FormActivity.class);
            startActivityForResult(intent, ACTIVITY_DO_REFRESH);
        });
        binding.tapFab.setOnClickListener(v -> {
            Intent intent = new Intent(WpiActivity.this, CartActivity.class);
            startActivityForResult(intent, ACTIVITY_DO_REFRESH);
        });

        syncDoorButtons();

        ViewModelProvider provider = new ViewModelProvider(this);

        combinedUserViewModel = provider.get(CombinedUserViewModel.class);
        combinedUserViewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        combinedUserViewModel.getData().observe(this, SuccessObserver.with(user -> {
            Picasso.get().load(user.getProfilePicture()).into(binding.profilePicture);
            String balance = currencyFormatter.format(user.getBalanceDecimal());
            String orders = decimalFormatter.format(user.getOrders());
            binding.profileDescription.setText(getString(R.string.wpi_user_description, balance, orders));
            setTitle();
            syncDoorButtons();
        }));

        DoorViewModel doorViewModel = provider.get(DoorViewModel.class);
        doorViewModel.getNetworkState().observe(this, networkState -> {
            boolean enabled = networkState == null || networkState == NetworkState.IDLE;

            // If the buttons are currently disabled, and we want to re-enable them,
            // we add a delay of about 3 seconds.
            // This scenario is most likely after a button was pressed, but the HTTP call
            // is much faster than the actual lock.
            // In the other case, just do it.
            if (enabled && (!binding.doorButtonOpen.isEnabled() || !binding.doorButtonClose.isEnabled())) {
                new Handler().postDelayed(() -> {
                    binding.doorButtonClose.setEnabled(true);
                    binding.doorButtonOpen.setEnabled(true);
                }, 3000);
            } else {
                binding.doorButtonClose.setEnabled(enabled);
                binding.doorButtonOpen.setEnabled(enabled);
            }
        });

        binding.doorButtonClose.setOnClickListener(v -> new MaterialAlertDialogBuilder(WpiActivity.this)
                .setMessage(R.string.wpi_door_close)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> doorViewModel.startRequest(DoorRequest.Command.CLOSE))
                .setNegativeButton(android.R.string.cancel, null)
                .show());
        binding.doorButtonOpen.setOnClickListener(v -> new MaterialAlertDialogBuilder(WpiActivity.this)
                .setMessage(R.string.wpi_door_open)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> doorViewModel.startRequest(DoorRequest.Command.OPEN))
                .setNegativeButton(android.R.string.cancel, null)
                .show());
    }

    private void setTitle() {
        setTitle(AccountManager.getUsername(this));
    }

    private void syncDoorButtons() {
        boolean hasDoorKey = !TextUtils.isEmpty(AccountManager.getDoorKey(this));
        binding.doorButtonOpen.setVisibility(hasDoorKey ? View.VISIBLE : View.GONE);
        binding.doorButtonClose.setVisibility(hasDoorKey ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.viewPager.registerOnPageChangeCallback(callback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.viewPager.unregisterOnPageChangeCallback(callback);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        // TODO: better error message.
        Snackbar.make(binding.getRoot(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> combinedUserViewModel.onRefresh())
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
            combinedUserViewModel.onRefresh();

            if (pageAdapter != null) {
                pageAdapter.notifyDataSetChanged();
            }
        }
    }
}
