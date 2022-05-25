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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.function.Consumer;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.wpi.tap.product.Product;
import be.ugent.zeus.hydra.wpi.tap.product.ProductAdapter;

/**
 * A dialog fragment allowing the user to search for and pick a product.
 *
 * @author Niko Strijbol
 */
public class ProductPickerDialogFragment extends DialogFragment implements Consumer<Product> {

    private CartInteraction interactor;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        interactor = (CartInteraction) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wpi_cart_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Modify view :(
        Rect displayRectangle = new Rect();
        Window window = requireActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        requireDialog().getWindow().setLayout((int) (displayRectangle.width() * 0.9f), (int) (displayRectangle.width() * 0.9f));

        ProductAdapter adapter = new ProductAdapter(this);
        RecyclerView recyclerView = ViewCompat.requireViewById(view, R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(requireContext()));
        recyclerView.setAdapter(adapter);

        SearchView searchView = ViewCompat.requireViewById(view, R.id.search_view);
        searchView.setOnQueryTextListener(adapter);
        searchView.setOnCloseListener(adapter);
        searchView.setOnSearchClickListener(v -> adapter.onOpen());

        // There must be a cart in the activity.
        // TODO: this is actually not the case when the activity is recreated.
        //   Either fix it in the activity, or handle it here.
        CartViewModel viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        viewModel.getLastSeenCart().observe(this, cart -> {
            adapter.submitData(new ArrayList<>(cart.getProductIdToProduct().values()));
            ViewCompat.requireViewById(view, R.id.progress_bar).setVisibility(View.GONE);
        });
    }

    @Override
    public void accept(Product product) {
        // When the item gets clicked.
        interactor.add(product);
        dismiss();
    }
}
