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

import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * View holder for the products in the Tap cart.
 *
 * @author Niko Strijbol
 */
class CartProductViewHolder extends DataViewHolder<CartProduct> implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    private final ImageView thumbnail;
    private final TextView title;
    private final TextView description;
    private final TextView meta;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private final CartInteraction cartInteraction;

    CartProductViewHolder(View v, CartInteraction cartInteraction) {
        super(v);
        thumbnail = v.findViewById(R.id.thumbnail);
        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        meta = v.findViewById(R.id.meta);
        currencyFormatter.setCurrency(Currency.getInstance("EUR"));
        itemView.setOnCreateContextMenuListener(this);
        this.cartInteraction = cartInteraction;
    }

    @Override
    public void populate(final CartProduct product) {
        title.setText(product.getName());
        PriorityUtils.loadThumbnail(itemView.getContext(), product.getThumbnail(), thumbnail);
        BigDecimal totalPrice = product.getPriceDecimal().multiply(BigDecimal.valueOf(product.getAmount()));
        meta.setText(currencyFormatter.format(totalPrice));
        String unitPrice = currencyFormatter.format(product.getPriceDecimal());
        description.setText(itemView.getContext().getString(R.string.wpi_cart_product_description, product.getAmount(), unitPrice));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(v.getContext());
        inflater.inflate(R.menu.menu_cart_item, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setOnMenuItemClickListener(this);
        }
        
        // Hide clear all based on count.
        CartProductAdapter adapter = (CartProductAdapter) getBindingAdapter();
        if (adapter == null) {
            return;
        }
        int position = getBindingAdapterPosition();
        if (position == NO_POSITION) {
            return;
        }
        CartProduct product = adapter.getItem(position);
        MenuItem item = menu.findItem(R.id.cart_minus);
        item.setVisible(product.getAmount() != 1);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        CartProductAdapter adapter = (CartProductAdapter) getBindingAdapter();
        if (adapter == null) {
            return false;
        }
        int position = getBindingAdapterPosition();
        if (position == NO_POSITION) {
            return false;
        }
        CartProduct product = adapter.getItem(position);
        if (item.getItemId() == R.id.cart_plus) {
            cartInteraction.increment(product);
            return true;
        } else if (item.getItemId() == R.id.cart_minus) {
            cartInteraction.decrement(product);
            return true;
        } else if (item.getItemId() == R.id.cart_delete) {
            cartInteraction.remove(product);
            return true;
        }
        return false;
    }
}
