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

package be.ugent.zeus.hydra.wpi.tab.list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.wpi.account.AccountManager;

/**
 * View holder for the products in the Tab fragment.
 *
 * @author Niko Strijbol
 */
class TransactionViewHolder extends DataViewHolder<Transaction> {

    private final ImageView thumbnail;
    private final TextView title;
    private final TextView firstDescription;
    private final TextView secondDescription;
    private final TextView meta;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private final String me;

    TransactionViewHolder(View v) {
        super(v);
        thumbnail = v.findViewById(R.id.thumbnail);
        title = v.findViewById(R.id.title);
        firstDescription = v.findViewById(R.id.firstDescription);
        secondDescription = v.findViewById(R.id.secondDescription);
        meta = v.findViewById(R.id.meta);
        currencyFormatter.setCurrency(Currency.getInstance("EUR"));
        me = AccountManager.getUsername(v.getContext());
    }

    @Override
    public void populate(final Transaction transaction) {
        title.setText(transaction.getDisplayOther(me));
        if (transaction.getIssuer().equals("Tap")) {
            thumbnail.setImageResource(R.drawable.logo_tap);
        } else if (transaction.getAmount() > 0) {
            thumbnail.setImageResource(R.drawable.ic_bank_transfer_out);
        } else if (transaction.getAmount() < 0) {
            thumbnail.setImageResource(R.drawable.ic_bank_transfer_in);
        } else {
            // This is probably not possible.
            thumbnail.setImageResource(R.drawable.ic_receipt_long);
        }
        meta.setText(currencyFormatter.format(transaction.getAdjustedAmount(me)));
        secondDescription.setText(transaction.getMessage());
        firstDescription.setText(DateUtils.relativeDateTimeString(transaction.getTime(), itemView.getContext()));
    }
    
}
