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

package be.ugent.zeus.hydra.resto.sandwich.ecological;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.common.utils.StringUtils;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * @author Niko Strijbol
 */
class EcologicalViewHolder extends DataViewHolder<EcologicalSandwich> {

    private final TextView name;
    private final TextView dates;
    private final ExpandableLayout expandableLayout;
    private final TextView ingredients;
    private final MultiSelectAdapter<EcologicalSandwich> adapter;

    EcologicalViewHolder(View itemView, MultiSelectAdapter<EcologicalSandwich> adapter) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        dates = itemView.findViewById(R.id.dates);
        expandableLayout = itemView.findViewById(R.id.expandable_layout);
        ingredients = itemView.findViewById(R.id.sandwich_ingredients);
        this.adapter = adapter;
    }

    @Override
    public void populate(EcologicalSandwich sandwich) {
        Context c = itemView.getContext();
        name.setText(sandwich.name());
        String start = DateUtils.friendlyDate(c, sandwich.start());
        String end = DateUtils.friendlyDate(c, sandwich.end());
        if (sandwich.vegan()) {
            Drawable image = AppCompatResources.getDrawable(c, R.drawable.resto_vegan);
            name.setCompoundDrawablesWithIntrinsicBounds(null, null, image, null);
        } else {
            name.setCompoundDrawables(null, null, null, null);
        }
        dates.setText(String.format(c.getString(R.string.date_between), start, end));
        String ingredientsString = StringUtils.formatList(sandwich.ingredients());
        ingredients.setText(String.format(c.getString(R.string.resto_sandwich_ingredients), ingredientsString));
        expandableLayout.setExpanded(adapter.isChecked(getBindingAdapterPosition()), false);
        itemView.setOnClickListener(v -> {
            adapter.setChecked(getBindingAdapterPosition());
            expandableLayout.toggle();
        });
    }
}
