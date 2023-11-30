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

package be.ugent.zeus.hydra.association.list;


import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

/**
 * Simple view holder.
 * <p>
 * Needs to be static because generics are stupid.
 */
class AssociationViewHolder extends DataViewHolder<Association> {

    private final CheckBox checkBox;
    private final LinearLayout parent;
    private final TextView title;

    private final MultiSelectAdapter<Association> adapter;

    AssociationViewHolder(View itemView, MultiSelectAdapter<Association> adapter) {

        super(itemView);
        this.adapter = adapter;

        this.checkBox = itemView.findViewById(R.id.checkbox);
        this.parent = itemView.findViewById(R.id.parent_layout);
        this.title = itemView.findViewById(R.id.title_checkbox);
    }

    @Override
    public void populate(Association data) {
        title.setText(data.name());
        checkBox.setChecked(adapter.isChecked(getBindingAdapterPosition()));
        parent.setOnClickListener(v -> {
            adapter.setChecked(getBindingAdapterPosition());
            checkBox.toggle();
        });
        checkBox.setOnClickListener(v -> adapter.setChecked(getBindingAdapterPosition()));
    }
}
