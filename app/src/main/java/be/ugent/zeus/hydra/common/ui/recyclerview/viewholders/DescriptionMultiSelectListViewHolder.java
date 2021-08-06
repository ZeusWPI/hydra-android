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

package be.ugent.zeus.hydra.common.ui.recyclerview.viewholders;


import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.function.Function;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;

/*
 * ViewHolder for MultiSelectLists with both a title and description for each item.
 * (And of course a checkbox)
 */
public class DescriptionMultiSelectListViewHolder<E> extends DataViewHolder<E> {

    private final CheckBox checkBox;
    private final LinearLayout parent;
    private final TextView title;
    private final TextView description;

    private final MultiSelectAdapter<E> adapter;

    private final Function<E, String> titleProvider;
    private final Function<E, String> descriptionProvider;

    public DescriptionMultiSelectListViewHolder(
            View itemView,
            MultiSelectAdapter<E> adapter,
            Function<E, String> titleProvider,
            Function<E, String> descriptionProvider
    ) {
        super(itemView);
        this.adapter = adapter;
        this.titleProvider = titleProvider;
        this.descriptionProvider = descriptionProvider;

        this.checkBox = itemView.findViewById(R.id.checkbox);
        this.parent = itemView.findViewById(R.id.parent_layout);
        this.title = itemView.findViewById(R.id.title_checkbox);
        this.description = itemView.findViewById(R.id.description_checkbox);
    }


    @Override
    public void populate(E data) {
        title.setText(titleProvider.apply(data));
        description.setText(descriptionProvider.apply(data));
        checkBox.setChecked(adapter.isChecked(getAdapterPosition()));
        parent.setOnClickListener(v -> {
            adapter.setChecked(getAdapterPosition());
            checkBox.toggle();
        });
        checkBox.setOnClickListener(v -> adapter.setChecked(getAdapterPosition()));
    }
}