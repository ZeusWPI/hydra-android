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

package be.ugent.zeus.hydra.wpi.tap.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.*;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.wpi.account.CombinedUserViewModel;
import be.ugent.zeus.hydra.wpi.tap.cart.CartActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.wpi.WpiActivity.ACTIVITY_DO_REFRESH;
import static be.ugent.zeus.hydra.wpi.tap.product.ProductData.PREF_SHOW_ONLY_IN_STOCK;

/**
 * Display TAP products.
 *
 * @author Niko Strijbol
 */
public class ProductFragment extends Fragment {

    private static final String SAVED_FAVOURITE = "saved_favourite";
    public static final String FAVOURITE_PINNED_SHORTCUT = "be.ugent.zeus.hydra.wpi.pinned_shortcut_favourite_tap";

    private static final String TAG = "ProductFragment";
    private ProductViewModel viewModel;
    private int favouriteProductId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            favouriteProductId = savedInstanceState.getInt(SAVED_FAVOURITE, -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(requireContext()));
        ProductAdapter adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(ColourUtils.resolveColour(requireContext(), R.attr.colorSecondary));

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        viewModel.getData().observe(getViewLifecycleOwner(), PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(getViewLifecycleOwner(), new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getFilteredData().observe(getViewLifecycleOwner(), new AdapterObserver<>(adapter));
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);

        // For refreshing, we request a refresh from the parent activity.
        // We then listen to the parent refresh state and refresh this fragment when the parent is refreshing.
        CombinedUserViewModel activityViewModel = new ViewModelProvider(requireActivity()).get(CombinedUserViewModel.class);
        swipeRefreshLayout.setOnRefreshListener(activityViewModel);
        activityViewModel.getRefreshing().observe(getViewLifecycleOwner(), refreshing -> {
            if (refreshing) {
                viewModel.onRefresh();
            }
        });

        // Attach both to the combiner.
        viewModel.getData().observe(getViewLifecycleOwner(), SuccessObserver.with(products -> viewModel.updateValue(products.getAllData())));
        activityViewModel.getData().observe(getViewLifecycleOwner(), SuccessObserver.with(combinedUser -> viewModel.updateValue(combinedUser.getFavourite())));

        // Listen to both.
        viewModel.getFavouriteProduct().observe(getViewLifecycleOwner(), pf -> {
            FloatingActionButton fab = requireActivity().findViewById(R.id.tap_add_favourite);
            if (pf.first == null || pf.second == null) {
                fab.hide();
            } else {
                Optional<Product> product = pf.first.stream().filter(p -> p.getId() == pf.second).findFirst();
                if (!product.isPresent()) {
                    // Oops.
                    fab.hide();
                    requireActivity().invalidateOptionsMenu();
                    favouriteProductId = -1;
                    maybeUpdateShortcut();
                    return;
                }
                Product favourite = product.get();
                favouriteProductId = favourite.getId();
                fab.setOnClickListener(v -> {
                    Intent intent = new Intent(requireActivity(), CartActivity.class);
                    intent.putExtra(CartActivity.ARG_FAVOURITE_PRODUCT_ID, favourite.getId());
                    startActivityForResult(intent, ACTIVITY_DO_REFRESH);
                });
                fab.show();
                requireActivity().invalidateOptionsMenu();
                maybeUpdateShortcut();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_wpi_products, menu);

        // Set saved preference for stock stuff.
        MenuItem item = menu.findItem(R.id.action_filter_stock);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean show = preferences.getBoolean(PREF_SHOW_ONLY_IN_STOCK, false);
        item.setChecked(show);

        // Hide or show based on whether the user has a favourite item or not.
        MenuItem pinItem = menu.findItem(R.id.action_pin_favourite);
        pinItem.setVisible(favouriteProductId != -1);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter_stock) {
            boolean checked = !item.isChecked();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            preferences.edit()
                    .putBoolean(PREF_SHOW_ONLY_IN_STOCK, checked)
                    .apply();
            item.setChecked(checked);
            viewModel.requestRefresh();
            return true;
        } else if (item.getItemId() == R.id.action_pin_favourite) {
            createOrUpdatePinnedShortcut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.requestRefresh())
                .show();
    }

    private ShortcutInfoCompat createShortcutInfo() {
        Intent intent = new Intent(Intent.ACTION_VIEW, null, requireActivity(), CartActivity.class);
        intent.putExtra(CartActivity.ARG_FAVOURITE_PRODUCT_ID, favouriteProductId);
        intent.putExtra(CartActivity.ARG_FROM_SHORTCUT, true);

        return new ShortcutInfoCompat.Builder(requireContext(), FAVOURITE_PINNED_SHORTCUT)
                .setShortLabel(getString(R.string.wpi_pin_description))
                .setIcon(IconCompat.createWithResource(requireContext(), R.drawable.shortcut_tap_favourite))
                .setIntent(intent)
                .build();
    }

    private boolean maybeUpdateShortcut() {
        boolean exists = ShortcutManagerCompat.getShortcuts(requireContext(), ShortcutManagerCompat.FLAG_MATCH_PINNED)
                .stream().anyMatch(sic -> sic.getId().equals(FAVOURITE_PINNED_SHORTCUT));

        if (!exists) {
            return false; // Nothing do to here.
        }

        if (favouriteProductId == -1) {
            List<String> ids = Collections.singletonList(FAVOURITE_PINNED_SHORTCUT);
            ShortcutManagerCompat.disableShortcuts(requireContext(), ids, getString(R.string.wpi_pin_no_favourite));
        } else {
            ShortcutInfoCompat info = createShortcutInfo();
            ShortcutManagerCompat.enableShortcuts(requireContext(), Collections.singletonList(info));
        }

        return true;
    }

    private void createOrUpdatePinnedShortcut() {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(requireContext())) {
            Snackbar.make(requireView(), getString(R.string.wpi_pin_unsupported), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        boolean updated = maybeUpdateShortcut();

        if (updated) {
            // We updated an existing shortcut, so nothing more to do here.
            Toast.makeText(requireContext(), R.string.wpi_pin_updated, Toast.LENGTH_LONG).show();
            return;
        }

        if (favouriteProductId == -1) {
            // Don't create a shortcut for a favourite item that doesn't exist.
            Toast.makeText(requireContext(), R.string.wpi_pin_no_favourite, Toast.LENGTH_LONG).show();
            return;
        }

        ShortcutInfoCompat info = createShortcutInfo();
        ShortcutManagerCompat.requestPinShortcut(requireContext(), info, null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SAVED_FAVOURITE, favouriteProductId);
        super.onSaveInstanceState(outState);
    }
}
