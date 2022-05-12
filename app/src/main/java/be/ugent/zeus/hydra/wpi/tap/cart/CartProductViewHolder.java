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

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;

/**
 * View holder for the products in the Tap cart.
 *
 * @author Niko Strijbol
 */
class CartProductViewHolder extends DataViewHolder<CartProduct> {

    private final ImageView thumbnail;
    private final TextView title;
    private final TextView description;
    private final TextView meta;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    CartProductViewHolder(View v) {
        super(v);
        thumbnail = v.findViewById(R.id.thumbnail);
        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        meta = v.findViewById(R.id.meta);
        currencyFormatter.setCurrency(Currency.getInstance("EUR"));
    }

    @Override
    public void populate(final CartProduct product) {
        title.setText(product.getName());
        PriorityUtils.loadThumbnail(itemView.getContext(), product.getThumbnailUrl(), thumbnail);
        BigDecimal totalPrice = product.getPriceDecimal().multiply(BigDecimal.valueOf(product.getAmount()));
        meta.setText(currencyFormatter.format(totalPrice));
        String unitPrice = currencyFormatter.format(product.getPriceDecimal());
        description.setText(itemView.getContext().getString(R.string.wpi_cart_product_description, product.getAmount(), unitPrice));
    }
}
