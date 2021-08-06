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

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.VisibleForTesting;

import java.time.LocalDate;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;


/**
 * For date headers.
 *
 * @author Niko Strijbol
 */
class DateHeaderViewHolder extends DataViewHolder<EventItem> {

    private final TextView headerText;

    DateHeaderViewHolder(View v) {
        super(v);
        headerText = v.findViewById(R.id.date_header);
    }

    @VisibleForTesting
    static String formatDate(Context context, LocalDate localDate) {
        return be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DateHeaderViewHolder.format(context, localDate);
    }

    @Override
    public void populate(EventItem eventItem) {
        headerText.setText(formatDate(headerText.getContext(), eventItem.getHeader()));
    }
}