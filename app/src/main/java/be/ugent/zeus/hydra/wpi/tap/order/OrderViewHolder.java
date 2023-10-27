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

package be.ugent.zeus.hydra.wpi.tap.order;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.common.utils.StringUtils;
import be.ugent.zeus.hydra.wpi.tap.product.Product;

/**
 * View holder for the products in the TAP fragment.
 *
 * @author Niko Strijbol
 */
public class OrderViewHolder extends DataViewHolder<Order> {

    private final TextView orderDescription;
    private final TextView orderDate;
    private final Button cancelButton;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private final Consumer<Order> onClickListener;

    OrderViewHolder(View v, @NonNull Consumer<Order> onClickListener) {
        super(v);
        orderDescription = v.findViewById(R.id.order_description);
        orderDate = v.findViewById(R.id.order_date);
        cancelButton = v.findViewById(R.id.cancel_button);
        currencyFormatter.setCurrency(Currency.getInstance("EUR"));
        this.onClickListener = onClickListener;
    }

    @Override
    public void populate(final Order order) {
        String total = currencyFormatter.format(order.getTotal());
        List<String> productNames = order.getProducts().stream().map(Product::getName).collect(Collectors.toList());
        String formattedProducts = StringUtils.formatList(productNames);
        String description = itemView.getContext().getString(R.string.wpi_tap_order_description, total, formattedProducts); 
        orderDescription.setText(description);
        
        CharSequence friendlyDate = DateUtils.relativeDateTimeString(order.getCreatedAt(), itemView.getContext());
        orderDate.setText(friendlyDate);

        // We expect a refresh will happen anyway, so don't care about un-disabling.
        cancelButton.setOnClickListener(v -> {
            itemView.setEnabled(false);
//                cancelButton.setEnabled(false);
            onClickListener.accept(order);
        });
    }
}
