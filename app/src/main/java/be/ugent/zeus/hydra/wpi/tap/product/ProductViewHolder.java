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

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.function.Consumer;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;

/**
 * View holder for the products in the TAP fragment.
 *
 * @author Niko Strijbol
 */
class ProductViewHolder extends DataViewHolder<Product> {

    private final ImageView thumbnail;
    private final TextView title;
    private final TextView description;
    private final TextView meta;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private final NumberFormat decimalFormatter = NumberFormat.getNumberInstance();
    private final Consumer<Product> onClickListener;

    ProductViewHolder(View v, @Nullable Consumer<Product> onClickListener) {
        super(v);
        thumbnail = v.findViewById(R.id.thumbnail);
        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        meta = v.findViewById(R.id.meta);
        currencyFormatter.setCurrency(Currency.getInstance("EUR"));
        this.onClickListener = onClickListener;
    }

    @Override
    public void populate(final Product product) {
        title.setText(product.getName());
        PriorityUtils.loadThumbnail(itemView.getContext(), product.getImageUrl(), thumbnail);
        meta.setText(currencyFormatter.format(product.getPriceDecimal()));
        String calories;
        if (product.getCalories() != null) {
            calories = decimalFormatter.format(product.getCalories()) + " kcal";
        } else {
            calories = itemView.getContext().getString(R.string.wpi_product_na);
        }
        description.setText(itemView.getContext().getString(R.string.wpi_product_description, calories, product.getStock()));
        if (onClickListener != null) {
            itemView.setOnClickListener(v -> onClickListener.accept(product));
        }
    }
}
