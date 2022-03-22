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

package be.ugent.zeus.hydra.resto.salad;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * @author Niko Strijbol
 */
class SaladHolder extends DataViewHolder<SaladBowl> {

    private final TextView name;
    private final TextView price;
    private final ExpandableLayout expandableLayout;
    private final TextView description;
    private final MultiSelectAdapter<SaladBowl> adapter;

    SaladHolder(View itemView, MultiSelectAdapter<SaladBowl> adapter) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        price = itemView.findViewById(R.id.price);
        expandableLayout = itemView.findViewById(R.id.expandable_layout);
        description = itemView.findViewById(R.id.description);
        this.adapter = adapter;
    }

    @Override
    public void populate(SaladBowl bowl) {
        Context c = itemView.getContext();
        name.setText(bowl.getName());
        price.setText(String.format(c.getString(R.string.resto_salad_price), bowl.getPrice()));
        description.setText(bowl.getDescription());
        expandableLayout.setExpanded(adapter.isChecked(getBindingAdapterPosition()), false);
        itemView.setOnClickListener(v -> {
            adapter.setChecked(getBindingAdapterPosition());
            expandableLayout.toggle();
        });
    }
}
