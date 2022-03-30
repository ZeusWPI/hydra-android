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

package be.ugent.zeus.hydra.info;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * View holder for info items.
 *
 * @author Niko Strijbol
 */
class InfoViewHolder extends DataViewHolder<InfoItem> {

    private static final String TAG = "InfoViewHolder";

    private final TextView title;
    private final ActivityHelper helper;

    InfoViewHolder(View v, ActivityHelper helper) {
        super(v);
        title = v.findViewById(R.id.info_name);
        this.helper = helper;
    }

    @Override
    public void populate(final InfoItem infoItem) {
        Context c = itemView.getContext();
        title.setText(infoItem.getTitle());
        itemView.setOnClickListener(v -> infoItem.getType().doOnClick(v.getContext(), helper, infoItem));

        Drawable more = infoItem.getType().getDrawable(itemView.getContext(), R.attr.colorPrimary);

        // If the item itself has an image.
        if (infoItem.getImage() != null) {
            int resId = c.getResources().getIdentifier(infoItem.getImage(), "drawable", c.getPackageName());
            if (resId == 0) {
                Log.e(TAG, "Icon for info item " + infoItem.getImage() + " was not found!");
                title.setCompoundDrawablesWithIntrinsicBounds(null, null, more, null);
            } else {
                try {
                    Drawable icon = ViewUtils.getTintedVectorDrawableAttr(c, resId, R.attr.colorPrimary);
                    title.setCompoundDrawablesWithIntrinsicBounds(icon, null, more, null);
                } catch (Resources.NotFoundException e) {
                    Log.w(TAG, "On non-weird devices, this should not occur.", e);
                    // Since it occurred anyway, ignore the error.
                    title.setCompoundDrawablesWithIntrinsicBounds(null, null, more, null);
                }
            }
        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(null, null, more, null);
        }
    }
}
