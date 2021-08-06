/*
 * Copyright (c) 2021 The Hydra authors
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

package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * @author Niko Strijbol
 */
class RegularHolder extends DataViewHolder<RegularSandwich> {

    private final TextView name;
    private final TextView smallPrice;
    private final TextView mediumPrice;
    private final ExpandableLayout expandableLayout;
    private final TextView ingredients;
    private final MultiSelectAdapter<RegularSandwich> adapter;

    RegularHolder(View itemView, MultiSelectAdapter<RegularSandwich> adapter) {
        super(itemView);

        name = itemView.findViewById(R.id.sandwich_name);
        mediumPrice = itemView.findViewById(R.id.sandwich_price_medium);
        smallPrice = itemView.findViewById(R.id.sandwich_price_small);
        expandableLayout = itemView.findViewById(R.id.expandable_layout);
        ingredients = itemView.findViewById(R.id.sandwich_ingredients);
        this.adapter = adapter;
    }

    @Override
    public void populate(RegularSandwich sandwich) {
        Context c = itemView.getContext();
        name.setText(sandwich.getName());
        mediumPrice.setText(String.format(c.getString(R.string.resto_sandwich_price_medium), sandwich.getPriceMedium()));
        smallPrice.setText(String.format(c.getString(R.string.resto_sandwich_price_small), sandwich.getPriceSmall()));
        String ingredientsString = TextUtils.join(", ", sandwich.getIngredients());
        ingredients.setText(String.format(c.getString(R.string.resto_sandwich_ingredients), ingredientsString));
        expandableLayout.setExpanded(adapter.isChecked(getAdapterPosition()), false);
        itemView.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            expandableLayout.toggle();
        });
    }
}
